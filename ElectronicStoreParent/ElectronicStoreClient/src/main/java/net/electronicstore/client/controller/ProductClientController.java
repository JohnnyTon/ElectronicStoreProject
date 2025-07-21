package net.electronicstore.client.controller;

import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;
import net.electronicstore.client.service.ProductClientService;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.dto.response.PageResponseDTO;
import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.enums.OperationStatus;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductClientController {

  private final ProductClientService productService;

  public ProductClientController(ProductClientService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<PageResponseDTO<ProductResponseDTO>>> getFilteredProducts(
      @RequestParam(required = false) String category,
      @RequestParam(required = false) BigDecimal min,
      @RequestParam(required = false) BigDecimal max,
      @RequestParam(required = false) Boolean available, Pageable pageable) {
    Page<ProductResponseDTO> filteredProducts =
        productService.getFilteredProducts(category, min, max, available, pageable);

    PageResponseDTO<ProductResponseDTO> response = new PageResponseDTO<>(
        filteredProducts.getContent(), filteredProducts.getNumber(), filteredProducts.getSize(),
        filteredProducts.getTotalElements(), filteredProducts.getTotalPages());

    return ResponseEntity.ok(
        new BaseResponseDTO<>(response, OperationStatus.SUCCESS, "Retrieve products successfully"));
  }
}
