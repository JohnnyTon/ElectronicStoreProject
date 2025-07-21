package net.electronicstore.client.service;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import net.electronicstore.common.dto.response.ProductResponseDTO;

public interface ProductClientService {
  Page<ProductResponseDTO> getFilteredProducts(String category, BigDecimal min, BigDecimal max,
      Boolean available, Pageable pageable);
}
