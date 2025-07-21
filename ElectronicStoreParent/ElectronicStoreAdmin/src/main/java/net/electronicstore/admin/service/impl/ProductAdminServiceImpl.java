package net.electronicstore.admin.service.impl;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import net.electronicstore.admin.service.ProductAdminService;
import net.electronicstore.common.dto.request.ProductRequestDTO;
import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.exception.ProductNotFoundException;
import net.electronicstore.common.repository.ProductRepository;
import net.electronicstore.common.util.ProductMapper;

@Slf4j
@Service
public class ProductAdminServiceImpl implements ProductAdminService {

  private final ProductRepository productRepository;

  public ProductAdminServiceImpl(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public ProductResponseDTO createProduct(ProductRequestDTO dto) {
    Product product = new Product();
    product.setName(dto.name());
    product.setDescription(dto.description());
    product.setPrice(dto.price());
    product.setCategory(dto.category());
    product.setAvailable(dto.available());
    product.setStock(dto.stock());

    Product saved = productRepository.save(product);

    return ProductMapper.toDto(saved);
  }

  @Override
  public void deleteProduct(Long id) {
    if (!productRepository.existsById(id)) {
      log.warn("Product with ID {} not found", id);
      throw new ProductNotFoundException(id);
    }
    productRepository.deleteById(id);
    log.info("Product with ID {} deleted", id);
  }

  @Override
  public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
    Product product =
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));

    if (dto.name() != null && !dto.name().isBlank()) {
      product.setName(dto.name());
    }

    if (dto.description() != null && !dto.description().isBlank()) {
      product.setDescription(dto.description());
    }

    if (dto.price() != null) {
      if (dto.price().compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Price must be greater than 0");
      }
      product.setPrice(dto.price());
    }

    if (dto.category() != null && !dto.category().isBlank()) {
      product.setCategory(dto.category());
    }

    if (dto.available() != null) {
      product.setAvailable(dto.available());
    }

    if (dto.stock() != null) {
      if (dto.stock() < 0) {
        throw new IllegalArgumentException("Stock must be non-negative");
      }
      product.setStock(dto.stock());
    }

    Product saved = productRepository.save(product);

    return ProductMapper.toDto(saved);
  }

  @Override
  public Page<ProductResponseDTO> getAllProducts(Pageable pageable) {
    Page<ProductResponseDTO> result = productRepository.findAll(pageable).map(ProductMapper::toDto);
    log.debug("Fetched {} products", result.getNumberOfElements());
    return result;
  }

  @Override
  public ProductResponseDTO getProductById(Long id) {
    return productRepository.findById(id).map(ProductMapper::toDto)
        .orElseThrow(() -> new ProductNotFoundException(id));
  }
}
