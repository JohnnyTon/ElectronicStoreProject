package net.electronicstore.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.electronicstore.admin.service.ProductAdminService;
import net.electronicstore.common.dto.request.ProductRequestDTO;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.dto.response.PageResponseDTO;
import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.enums.OperationStatus;

@Slf4j
@RestController
@RequestMapping("/products")
public class ProductAdminController {

  private final ProductAdminService productService;

  public ProductAdminController(ProductAdminService productService) {
    this.productService = productService;
  }

  @PostMapping
  public ResponseEntity<BaseResponseDTO<ProductResponseDTO>> createProduct(
      @RequestBody ProductRequestDTO dto) {
    log.info("Received request to create product: {}", dto);
    ProductResponseDTO response = productService.createProduct(dto);
    log.debug("Product created successfully: {}", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponseDTO<>(response,
        OperationStatus.SUCCESS, "Added new product " + dto.name()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<BaseResponseDTO<Object>> deleteProduct(@PathVariable Long id) {
    log.info("Request to delete product with ID: {}", id);
    productService.deleteProduct(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new BaseResponseDTO<>(null,
        OperationStatus.SUCCESS, "Successfully removed product ID " + id));
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<PageResponseDTO<ProductResponseDTO>>> getAllProducts(
      Pageable pageable) {
    log.info("Fetching paginated list of all products with params: {}", pageable);
    Page<ProductResponseDTO> page = productService.getAllProducts(pageable);

    PageResponseDTO<ProductResponseDTO> response = new PageResponseDTO<>(page.getContent(),
        page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());

    log.debug("Retrieved {} products (page {}/{})", page.getNumberOfElements(), page.getNumber() + 1, page.getTotalPages());
    return ResponseEntity
        .ok(new BaseResponseDTO<>(response, OperationStatus.SUCCESS, "Products retrieved"));
  }
}
