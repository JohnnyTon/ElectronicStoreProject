package net.electronicstore.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import net.electronicstore.admin.service.impl.ProductAdminServiceImpl;
import net.electronicstore.common.dto.request.ProductRequestDTO;
import net.electronicstore.common.dto.response.ProductResponseDTO;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.exception.ProductNotFoundException;
import net.electronicstore.common.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductAdminServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductAdminServiceImpl productService;

  @Test
  void testCreateProduct() {
    ProductRequestDTO dto =
        new ProductRequestDTO("Test", "Desc", new BigDecimal("100"), "Cat", true, 5);

    Product product = new Product();
    product.setId(1L);
    product.setName(dto.name());

    when(productRepository.save(any(Product.class))).thenReturn(product);

    ProductResponseDTO result = productService.createProduct(dto);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(productRepository, times(1)).save(any(Product.class));
  }

  @Test
  void testUpdateProduct() {
    Long id = 1L;
    Product existing = new Product();
    existing.setId(id);
    existing.setName("Old");

    ProductRequestDTO dto =
        new ProductRequestDTO("New", "NewDesc", new BigDecimal("200"), "NewCat", false, 10);

    when(productRepository.findById(id)).thenReturn(Optional.of(existing));
    when(productRepository.save(any(Product.class))).thenReturn(existing);

    ProductResponseDTO result = productService.updateProduct(id, dto);

    assertEquals("New", result.name());
    assertEquals("NewDesc", result.description());
    assertEquals("NewCat", result.category());
    assertFalse(result.available());
    assertEquals(10, result.stock());

    verify(productRepository).findById(id);
    verify(productRepository).save(existing);
  }

  @Test
  void testUpdateProductNotFound() {
    when(productRepository.findById(1L)).thenReturn(Optional.empty());

    ProductRequestDTO dto = new ProductRequestDTO("A", "B", BigDecimal.TEN, "Cat", true, 1);

    Exception ex = assertThrows(ProductNotFoundException.class, () -> {
      productService.updateProduct(1L, dto);
    });

    assertEquals("Product not found with ID: " + 1L, ex.getMessage());
  }

  @Test
  void testGetAllProducts() {
    List<Product> products = Arrays.asList(new Product(), new Product());
    Page<Product> productPage = new PageImpl<>(products);

    Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
    when(productRepository.findAll(pageable)).thenReturn(productPage);

    Page<ProductResponseDTO> result = productService.getAllProducts(pageable);

    assertEquals(2, result.getTotalElements());
    verify(productRepository).findAll(pageable);
  }

  @Test
  void testGetProductById() {
    Product product = new Product();
    product.setId(1L);
    product.setName("iPhone");

    when(productRepository.findById(1L)).thenReturn(Optional.of(product));

    ProductResponseDTO result = productService.getProductById(1L);

    assertEquals("iPhone", result.name());
    verify(productRepository).findById(1L);
  }

  @Test
  void testGetProductByIdNotFound() {
    when(productRepository.findById(999L)).thenReturn(Optional.empty());

    Exception ex = assertThrows(ProductNotFoundException.class, () -> {
      productService.getProductById(999L);
    });

    assertEquals("Product not found with ID: " + 999L, ex.getMessage());
  }

  @Test
  void testDeleteProductById() {
    when(productRepository.existsById(1L)).thenReturn(true);
    doNothing().when(productRepository).deleteById(1L);

    productService.deleteProduct(1L);

    verify(productRepository).deleteById(1L);
  }

  @Test
  void testDeleteProductNotFound() {
    when(productRepository.existsById(1L)).thenReturn(false);

    Exception ex = assertThrows(ProductNotFoundException.class, () -> {
      productService.deleteProduct(1L);
    });

    assertEquals("Product not found with ID: " + 1L, ex.getMessage());
  }
}
