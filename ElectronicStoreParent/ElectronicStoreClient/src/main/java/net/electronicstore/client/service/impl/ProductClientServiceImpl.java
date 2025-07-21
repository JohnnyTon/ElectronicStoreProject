package net.electronicstore.client.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import net.electronicstore.client.service.ProductClientService;
import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.repository.ProductRepository;
import net.electronicstore.common.util.ProductMapper;

@Slf4j
@Service
public class ProductClientServiceImpl implements ProductClientService {

  private final ProductRepository productRepository;

  public ProductClientServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  public Page<ProductResponseDTO> getFilteredProducts(String category, BigDecimal min,
      BigDecimal max, Boolean available, Pageable pageable) {

    log.info("Filtering products - category: {}, min: {}, max: {}, available: {}, pageable: {}",
        category, min, max, available, pageable);

    Page<Product> products = productRepository.findAll((root, query, cb) -> {
      var predicates = new ArrayList<Predicate>();

      if (category != null) {
        predicates.add(cb.equal(root.get("category"), category));
      }
      if (min != null && max != null) {
        predicates.add(cb.between(root.get("price"), min, max));
      } else if (min != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("price"), min));
      } else if (max != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("price"), max));
      }
      if (available != null) {
        predicates.add(cb.equal(root.get("available"), available));
      }

      log.debug("Built predicates: {}", predicates);
      return cb.and(predicates.toArray(new Predicate[0]));
    }, pageable);

    log.debug("Retrieved {} products (page {}/{})", products.getNumberOfElements(),
        products.getNumber() + 1, products.getTotalPages());

    return products.map(ProductMapper::toDto);
  }
}
