package net.electronicstore.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import net.electronicstore.client.exception.OutOfStockException;
import net.electronicstore.client.service.impl.BasketServiceImpl;
import net.electronicstore.common.dto.ReceiptDTO;
import net.electronicstore.common.dto.request.BasketItemRequestDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;
import net.electronicstore.common.entity.Basket;
import net.electronicstore.common.entity.BasketItem;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.enums.DiscountType;
import net.electronicstore.common.exception.ProductNotFoundException;
import net.electronicstore.common.repository.BasketItemRepository;
import net.electronicstore.common.repository.BasketRepository;
import net.electronicstore.common.repository.ProductRepository;
import net.electronicstore.common.repository.ReceiptRepository;

@ExtendWith(MockitoExtension.class)
class BasketServiceImplTest {

  @InjectMocks
  private BasketServiceImpl basketService;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private BasketRepository basketRepository;

  @Mock
  private BasketItemRepository basketItemRepository;

  @Mock
  private ReceiptRepository receiptRepository;

  private UUID customerId;
  private Product product;
  private Basket basket;
  private BasketItemRequestDTO requestDTO;

  @BeforeEach
  void setUp() {
    customerId = UUID.fromString("97f521f9-b27b-4b71-aa89-08a0b239a635");

    product = new Product();
    product.setId(1L);
    product.setStock(10);
    product.setAvailable(true);
    product.setName("Test Product");
    product.setPrice(BigDecimal.TEN);

    requestDTO = new BasketItemRequestDTO(product.getId(), 3, customerId.toString());

    basket = new Basket();
    basket.setCustomerId(customerId);
    basket.setItems(new ArrayList<>());
  }

  @Test
  void addToBasket_shouldAddNewItemSuccessfully() {
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    var result = basketService.addToBasket(requestDTO);

    assertEquals(customerId.toString(), result.customerId());
    assertEquals(1, result.items().size());
    assertEquals(7, product.getStock());
    verify(productRepository).save(product);
  }

  @Test
  void addToBasket_shouldIncreaseQuantityIfItemExists() {
    BasketItem existingItem = new BasketItem();
    existingItem.setProduct(product);
    existingItem.setQuantity(2);
    basket.getItems().add(existingItem);

    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.save(any(Product.class))).thenReturn(product);

    var result = basketService.addToBasket(requestDTO);

    assertEquals(5, result.items().get(0).quantity());
    assertEquals(7, product.getStock());
  }

  @Test
  void addToBasket_shouldCreateBasketIfNotExists() {
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
    when(basketRepository.save(any(Basket.class))).thenReturn(basket);
    when(productRepository.save(any(Product.class))).thenReturn(product);

    var result = basketService.addToBasket(requestDTO);

    assertEquals(customerId.toString(), result.customerId());
    assertEquals(1, result.items().size());
  }

  @Test
  void addToBasket_shouldThrowExceptionIfOutOfStock() {
    product.setStock(1);
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    assertThrows(OutOfStockException.class, () -> basketService.addToBasket(requestDTO));
  }

  @Test
  void addToBasket_shouldThrowExceptionIfProductNotFound() {
    when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class, () -> basketService.addToBasket(requestDTO));
  }

  @Test
  void removeFromBasket_shouldRemoveItemCompletely_whenQuantityMatches() {
    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(3);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    BasketItemRequestDTO request = new BasketItemRequestDTO(product.getId(), 3, customerId.toString());

    BasketResponseDTO result = basketService.removeFromBasket(request);

    assertNotNull(result);
    assertTrue(basket.getItems().isEmpty());
    verify(basketItemRepository).delete(item);
    verify(productRepository).save(product);
  }

  @Test
  void removeFromBasket_shouldReduceQuantity_whenQuantityIsLess() {
    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(5);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    BasketItemRequestDTO request = new BasketItemRequestDTO(product.getId(), 2, customerId.toString());

    BasketResponseDTO result = basketService.removeFromBasket(request);

    assertNotNull(result);
    assertEquals(1, result.items().size());
    assertEquals(3, result.items().get(0).quantity());
    verify(productRepository).save(product);
    verify(basketItemRepository, never()).delete(any());
  }

  @Test
  void removeFromBasket_shouldThrowException_ifCartNotFound() {
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

    BasketItemRequestDTO request = new BasketItemRequestDTO(product.getId(), 1, customerId.toString());

    assertThrows(IllegalArgumentException.class, () -> basketService.removeFromBasket(request));
  }

  @Test
  void removeFromBasket_shouldThrowException_ifProductNotFound() {
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

    BasketItemRequestDTO request = new BasketItemRequestDTO(product.getId(), 1, customerId.toString());

    assertThrows(IllegalArgumentException.class, () -> basketService.removeFromBasket(request));
  }

  @Test
  void removeFromBasket_shouldThrowException_ifItemNotFoundInCart() {
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));
    when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

    BasketItemRequestDTO request = new BasketItemRequestDTO(product.getId(), 1, customerId.toString());

    assertThrows(IllegalArgumentException.class, () -> basketService.removeFromBasket(request));
  }


  @Test
  void getBasket_shouldReturnCartSuccessfully() {
    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(2);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));

    var result = basketService.getBasket(customerId.toString());

    assertNotNull(result);
    assertEquals(customerId.toString(), result.customerId());
    assertEquals(1, result.items().size());
    assertEquals(product.getId(), result.items().get(0).productId());
    assertEquals(2, result.items().get(0).quantity());
  }

  @Test
  void getBasket_shouldThrowExceptionIfCartNotFound() {
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> basketService.getBasket(customerId.toString()));
    assertTrue(ex.getMessage().contains("Basket not found for customer: " + customerId.toString()));
  }

  @Test
  void checkout_shouldThrowException_whenCartNotFound() {
    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> basketService.checkout(customerId.toString()));
  }

  @Test
  void checkout_shouldWorkWithoutDeals() {
    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(2);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));

    ReceiptDTO result = basketService.checkout(customerId.toString());

    assertNotNull(result);
    assertEquals(1, result.items().size());
    assertEquals(product.getPrice().multiply(BigDecimal.valueOf(2)), result.total());
    verify(receiptRepository).save(any());
    verify(basketRepository).save(basket);
    assertTrue(basket.getItems().isEmpty());
  }

  @Test
  void checkout_shouldApplyPercentageDeal() {
    DiscountDeal deal = new DiscountDeal();
    deal.setType(DiscountType.PERCENTAGE);
    deal.setDiscountPercent(BigDecimal.valueOf(20));
    deal.setExpiresAt(LocalDateTime.now().plusDays(1));
    product.getDiscountDeals().add(deal);

    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(1);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));

    ReceiptDTO result = basketService.checkout(customerId.toString());

    BigDecimal expectedTotal = product.getPrice().multiply(BigDecimal.valueOf(0.80)); // 20% off
    assertEquals(0, expectedTotal.compareTo(result.total()));
  }

  @Test
  void checkout_shouldApplyBuyXGetYDeal() {
    DiscountDeal deal = new DiscountDeal();
    deal.setType(DiscountType.BUY_X_GET_Y);
    deal.setBuyQuantity(2);
    deal.setGetQuantity(1);
    deal.setExpiresAt(LocalDateTime.now().plusDays(1));
    product.getDiscountDeals().add(deal);

    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(6); // Expect 2 free items (6 / 3 * 1)
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));

    ReceiptDTO result = basketService.checkout(customerId.toString());

    BigDecimal expectedTotal = product.getPrice().multiply(BigDecimal.valueOf(4)); // 2 free
    assertEquals(0, expectedTotal.compareTo(result.total()));
  }

  @Test
  void checkout_shouldClearCartAndSaveReceipt() {
    BasketItem item = new BasketItem();
    item.setProduct(product);
    item.setQuantity(1);
    basket.getItems().add(item);

    when(basketRepository.findByCustomerId(customerId)).thenReturn(Optional.of(basket));

    ReceiptDTO result = basketService.checkout(customerId.toString());

    verify(receiptRepository).save(any());
    verify(basketRepository).save(basket);
    assertTrue(basket.getItems().isEmpty());
  }

}
