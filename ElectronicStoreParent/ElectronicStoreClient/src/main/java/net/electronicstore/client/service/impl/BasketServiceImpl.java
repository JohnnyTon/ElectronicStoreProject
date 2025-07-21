package net.electronicstore.client.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.electronicstore.client.exception.OutOfStockException;
import net.electronicstore.client.service.BasketService;
import net.electronicstore.common.dto.ReceiptDTO;
import net.electronicstore.common.dto.ReceiptItemDTO;
import net.electronicstore.common.dto.request.BasketItemRequestDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;
import net.electronicstore.common.entity.Basket;
import net.electronicstore.common.entity.BasketItem;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.entity.Receipt;
import net.electronicstore.common.entity.ReceiptLine;
import net.electronicstore.common.enums.DiscountType;
import net.electronicstore.common.exception.ProductNotFoundException;
import net.electronicstore.common.repository.BasketItemRepository;
import net.electronicstore.common.repository.BasketRepository;
import net.electronicstore.common.repository.ProductRepository;
import net.electronicstore.common.repository.ReceiptRepository;
import net.electronicstore.common.util.BasketMapper;

@Slf4j
@Service
public class BasketServiceImpl implements BasketService {

  private final ProductRepository productRepository;
  private final BasketRepository basketRepository;
  private final BasketItemRepository basketItemRepository;
  private final ReceiptRepository receiptRepository;

  public BasketServiceImpl(ProductRepository productRepository, BasketRepository basketRepository,
      BasketItemRepository basketItemRepository, ReceiptRepository receiptRepository) {
    this.productRepository = productRepository;
    this.basketRepository = basketRepository;
    this.basketItemRepository = basketItemRepository;
    this.receiptRepository = receiptRepository;
  }

  @Override
  @Transactional
  public BasketResponseDTO addToBasket(BasketItemRequestDTO requestDTO) {
    UUID customerId = UUID.fromString(requestDTO.customerId());
    log.info("Adding product {} (qty: {}) to basket for customer {}", requestDTO.productId(), requestDTO.quantity(), customerId);

    Product product = productRepository.findById(requestDTO.productId())
        .orElseThrow(() -> new ProductNotFoundException(requestDTO.productId()));

    if (!product.isAvailable() || product.getStock() < requestDTO.quantity()) {
      log.warn("Product {} is out of stock or insufficient quantity. Requested: {}, Available: {}", 
               requestDTO.productId(), requestDTO.quantity(), product.getStock());
      throw new OutOfStockException("Product is out of stock or insufficient quantity");
    }

    product.setStock(product.getStock() - requestDTO.quantity());
    productRepository.save(product);

    Basket basket = basketRepository.findByCustomerId(customerId).orElseGet(() -> {
      Basket newBasket = new Basket();
      newBasket.setCustomerId(customerId);
      log.debug("Creating new basket for customer {}", customerId);
      return basketRepository.save(newBasket);
    });

    BasketItem existingItem = basket.getItems().stream()
        .filter(i -> i.getProduct().getId().equals(product.getId())).findFirst().orElse(null);

    if (existingItem != null) {
      log.debug("Product already in basket, increasing quantity");
      existingItem.setQuantity(existingItem.getQuantity() + requestDTO.quantity());
    } else {
      log.info("Adding new item to basket");
      BasketItem newItem = new BasketItem();
      newItem.setBasket(basket);
      newItem.setProduct(product);
      newItem.setQuantity(requestDTO.quantity());
      basket.getItems().add(newItem);
    }

    log.info("Item successfully added to basket for customer {}", customerId);
    return BasketMapper.toDto(basket);
  }

  @Override
  @Transactional
  public BasketResponseDTO removeFromBasket(BasketItemRequestDTO requestDTO) {
    UUID customerId = UUID.fromString(requestDTO.customerId());
    log.info("Removing product {} (qty: {}) from basket for customer {}", requestDTO.productId(), requestDTO.quantity(), customerId);

    Basket basket = basketRepository.findByCustomerId(customerId).orElseThrow(
        () -> new IllegalArgumentException("Basket not found for customer: " + customerId));

    Product product = productRepository.findById(requestDTO.productId())
        .orElseThrow(() -> new IllegalArgumentException("Product not found"));

    BasketItem item =
        basket.getItems().stream().filter(i -> i.getProduct().getId().equals(product.getId()))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Item not found in basket"));

    if (requestDTO.quantity() >= item.getQuantity()) {
      log.debug("Removing item completely from basket");
      basket.getItems().remove(item);
      basketItemRepository.delete(item);
      product.setStock(product.getStock() + item.getQuantity());
    } else {
      log.debug("Reducing quantity of item in basket");
      item.setQuantity(item.getQuantity() - requestDTO.quantity());
      product.setStock(product.getStock() + requestDTO.quantity());
    }

    productRepository.save(product);
    log.info("Item successfully removed/updated in basket for customer {}", customerId);
    return BasketMapper.toDto(basket);
  }

  @Override
  @Transactional(readOnly = true)
  public BasketResponseDTO getBasket(String customerId) {
    UUID customerUUID = UUID.fromString(customerId);
    log.info("Fetching basket for customer {}", customerId);

    Basket basket = basketRepository.findByCustomerId(customerUUID).orElseThrow(
        () -> new IllegalArgumentException("Basket not found for customer: " + customerId));

    return BasketMapper.toDto(basket);
  }

  @Override
  @Transactional
  public ReceiptDTO checkout(String customerId) {
    UUID customerUUID = UUID.fromString(customerId);
    log.info("Starting checkout for customer {}", customerId);

    Basket basket = basketRepository.findByCustomerId(customerUUID)
        .orElseThrow(() -> new IllegalArgumentException("Basket not found"));

    List<ReceiptItemDTO> receiptItems = new ArrayList<>();
    BigDecimal total = BigDecimal.ZERO;

    Receipt receiptEntity = new Receipt();
    receiptEntity.setCustomerId(customerUUID);

    for (BasketItem item : basket.getItems()) {
      Product product = item.getProduct();
      int quantity = item.getQuantity();
      BigDecimal unitPrice = product.getPrice();
      BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
      String appliedDeal = null;

      DiscountDeal deal = product.getDiscountDeals().stream()
          .filter(d -> d.getExpiresAt().isAfter(LocalDateTime.now())).findFirst().orElse(null);

      if (deal != null) {
        log.debug("Applying deal {} to product {}", deal.getType(), product.getId());
        if (deal.getType() == DiscountType.PERCENTAGE) {
          BigDecimal discount =
              subtotal.multiply(deal.getDiscountPercent().divide(BigDecimal.valueOf(100)));
          subtotal = subtotal.subtract(discount);
          appliedDeal = deal.getDiscountPercent().intValue() + "% OFF";
        } else if (deal.getType() == DiscountType.BUY_X_GET_Y) {
          int groupSize = deal.getBuyQuantity() + deal.getGetQuantity();
          int freeItems = (quantity / groupSize) * deal.getGetQuantity();
          int payableQuantity = quantity - freeItems;
          subtotal = unitPrice.multiply(BigDecimal.valueOf(payableQuantity));
          appliedDeal = "Buy " + deal.getBuyQuantity() + " Get " + deal.getGetQuantity() + " Free";
        }
      }

      total = total.add(subtotal);

      receiptItems.add(new ReceiptItemDTO(product.getId(), product.getName(), quantity, unitPrice,
          subtotal, appliedDeal));

      ReceiptLine line = new ReceiptLine();
      line.setReceipt(receiptEntity);
      line.setProduct(product);
      line.setQuantity(quantity);
      line.setUnitPrice(unitPrice);
      line.setSubtotal(subtotal);
      line.setAppliedDeal(appliedDeal);
      receiptEntity.getLines().add(line);
    }

    receiptEntity.setTotal(total);
    receiptRepository.save(receiptEntity);
    log.info("Checkout complete. Receipt ID: {}, Total: {}", receiptEntity.getId(), total);

    basket.getItems().clear();
    basketRepository.save(basket);
    log.debug("Basket cleared after checkout for customer {}", customerId);

    return new ReceiptDTO(customerId, receiptItems, total);
  }
}
