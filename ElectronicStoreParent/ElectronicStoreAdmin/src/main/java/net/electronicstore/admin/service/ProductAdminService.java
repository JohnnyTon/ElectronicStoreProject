package net.electronicstore.admin.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import net.electronicstore.common.dto.request.ProductRequestDTO;
import net.electronicstore.common.dto.response.ProductResponseDTO;

public interface ProductAdminService {
  ProductResponseDTO createProduct(ProductRequestDTO requestDTO);
  ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);
  void deleteProduct(Long id);
  Page<ProductResponseDTO> getAllProducts(Pageable pageable);
  ProductResponseDTO getProductById(Long id);
}
