package net.electronicstore.common.util;

import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.entity.Product;

public class ProductMapper {

  public static ProductResponseDTO toDto(Product product) {
    return new ProductResponseDTO(product.getId(), product.getName(), product.getDescription(),
        product.getPrice(), product.getCategory(), product.isAvailable(), product.getStock());
  }
}
