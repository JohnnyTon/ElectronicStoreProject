package net.electronicstore.admin.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import net.electronicstore.admin.exception.InvalidDealException;
import net.electronicstore.admin.service.DiscountDealAdminService;
import net.electronicstore.common.dto.request.DiscountDealRequestDTO;
import net.electronicstore.common.dto.response.DiscountDealResponseDTO;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.repository.DiscountDealRepository;
import net.electronicstore.common.repository.ProductRepository;
import net.electronicstore.common.util.DiscountDealMapper;

@Slf4j
@Service
public class DiscountDealAdminServiceImpl implements DiscountDealAdminService {

  private final ProductRepository productRepository;
  private final DiscountDealRepository discountDealRepository;

  public DiscountDealAdminServiceImpl(ProductRepository productRepository,
      DiscountDealRepository discountDealRepository) {
    this.productRepository = productRepository;
    this.discountDealRepository = discountDealRepository;
  }

  @Override
  public DiscountDealResponseDTO createDiscountDeal(DiscountDealRequestDTO dto) {
    Set<Product> products = new HashSet<>(productRepository.findAllById(dto.productIds()));
    log.debug("Fetched {} valid products for deal", products.size());

    if (products.isEmpty()) {
      log.warn("No valid products found for IDs: {}", dto.productIds());
      throw new InvalidDealException("No valid products found for given IDs");
    }

    if (dto.expiresAt() == null || dto.expiresAt().isBefore(LocalDateTime.now())) {
      log.warn("Invalid expiration date: {}", dto.expiresAt());
      throw new InvalidDealException("Expiration date must be in the future");
    }

    switch (dto.type()) {
      case PERCENTAGE -> validatePercentageDeal(dto);
      case BUY_X_GET_Y -> validateBxgyDeal(dto);
      default -> {
        log.error("Unsupported discount type: {}", dto.type());
        throw new InvalidDealException("Unsupported discount type: " + dto.type());
      }
    }

    DiscountDeal deal = new DiscountDeal();
    deal.setProducts(products);
    deal.setType(dto.type());
    deal.setBuyQuantity(dto.buyQuantity());
    deal.setGetQuantity(dto.getQuantity());
    deal.setDiscountPercent(dto.discountPercent());
    deal.setExpiresAt(dto.expiresAt());

    // Update both sides of the relationship
    products.forEach(p -> p.getDiscountDeals().add(deal));

    DiscountDeal saved = discountDealRepository.save(deal);

    return DiscountDealMapper.toResponse(saved);
  }

  private void validatePercentageDeal(DiscountDealRequestDTO dto) {
    log.debug("Validating percentage deal: {}", dto);
    if (dto.discountPercent() == null) {
      throw new InvalidDealException("Discount percent must be provided for PERCENTAGE deals");
    }
    if (dto.discountPercent().compareTo(BigDecimal.ZERO) <= 0
        || dto.discountPercent().compareTo(BigDecimal.valueOf(100)) > 0) {
      throw new InvalidDealException("Discount percent must be between 0 and 100");
    }
    if (dto.buyQuantity() != 0 || dto.getQuantity() != 0) {
      throw new InvalidDealException("BUY_X_GET_Y fields must be 0 for PERCENTAGE deals");
    }
  }

  private void validateBxgyDeal(DiscountDealRequestDTO dto) {
    log.debug("Validating BUY_X_GET_Y deal: {}", dto);
    if (dto.buyQuantity() <= 0 || dto.getQuantity() <= 0) {
      throw new InvalidDealException("Buy/Get quantities must be > 0 for BUY_X_GET_Y deals");
    }
    if (dto.discountPercent() != null) {
      throw new InvalidDealException("Discount percent must not be set for BUY_X_GET_Y deals");
    }
  }

  @Override
  public List<DiscountDealResponseDTO> getActiveDealsForProduct(Long productId) {
    log.info("Fetching active deals for product ID: {}", productId);

    List<DiscountDeal> deals =
        discountDealRepository.findActiveDealsByProductId(productId, LocalDateTime.now());

    if (deals.isEmpty()) {
      log.warn("No active deals found for product ID: {}", productId);
      throw new InvalidDealException("No deals found for given product ID");
    }

    log.debug("Found {} active deals for product ID: {}", deals.size(), productId);
    return deals.stream().map(DiscountDealMapper::toResponse).toList();
  }
}
