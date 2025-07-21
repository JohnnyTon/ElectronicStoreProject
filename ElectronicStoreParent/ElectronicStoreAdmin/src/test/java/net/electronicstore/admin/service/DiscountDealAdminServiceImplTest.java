package net.electronicstore.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import net.electronicstore.admin.exception.InvalidDealException;
import net.electronicstore.admin.service.impl.DiscountDealAdminServiceImpl;
import net.electronicstore.common.dto.request.DiscountDealRequestDTO;
import net.electronicstore.common.dto.response.DiscountDealResponseDTO;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.enums.DiscountType;
import net.electronicstore.common.repository.DiscountDealRepository;
import net.electronicstore.common.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class DiscountDealAdminServiceImplTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private DiscountDealRepository discountDealRepository;

  @InjectMocks
  private DiscountDealAdminServiceImpl discountDealService;

  private Product product;

  @BeforeEach
  void setUp() {
    product = new Product();
    product.setId(1L);
    product.setName("iPhone");
    product.setPrice(BigDecimal.valueOf(1299.99));
  }

  @Test
  void testCreatePercentageDealSuccess() {
    DiscountDealRequestDTO dto = new DiscountDealRequestDTO(Set.of(1L), DiscountType.PERCENTAGE,
        BigDecimal.valueOf(15), 0, 0, LocalDateTime.now().plusDays(5));

    when(productRepository.findAllById(dto.productIds())).thenReturn(List.of(product));

    // Mock saved deal
    DiscountDeal savedDeal = new DiscountDeal();
    savedDeal.setId(101L);
    savedDeal.setType(DiscountType.PERCENTAGE);
    savedDeal.setDiscountPercent(BigDecimal.valueOf(15));
    savedDeal.setExpiresAt(dto.expiresAt());
    savedDeal.setProducts(Set.of(product));

    when(discountDealRepository.save(any())).thenReturn(savedDeal);

    DiscountDealResponseDTO response = discountDealService.createDiscountDeal(dto);

    assertNotNull(response);
    assertEquals(DiscountType.PERCENTAGE, response.type());
    assertEquals(BigDecimal.valueOf(15), response.discountPercent());
    assertEquals(dto.expiresAt(), response.expiresAt());
    assertTrue(response.productIds().contains(1L));
  }


  @Test
  void testCreateBXGYSuccess() {
    DiscountDealRequestDTO dto = new DiscountDealRequestDTO(Set.of(1L), DiscountType.BUY_X_GET_Y,
        null, 2, 1, LocalDateTime.now().plusDays(7));

    when(productRepository.findAllById(dto.productIds())).thenReturn(List.of(product));

    // Mock saved deal
    DiscountDeal savedDeal = new DiscountDeal();
    savedDeal.setId(100L);
    savedDeal.setType(DiscountType.BUY_X_GET_Y);
    savedDeal.setBuyQuantity(2);
    savedDeal.setGetQuantity(1);
    savedDeal.setExpiresAt(dto.expiresAt());
    savedDeal.setProducts(Set.of(product));

    when(discountDealRepository.save(any())).thenReturn(savedDeal);

    DiscountDealResponseDTO response = discountDealService.createDiscountDeal(dto);

    assertNotNull(response);
    assertEquals(DiscountType.BUY_X_GET_Y, response.type());
    assertEquals(2, response.buyQuantity());
    assertEquals(1, response.getQuantity());
    assertEquals(dto.expiresAt(), response.expiresAt());
    assertTrue(response.productIds().contains(1L));
  }


  @Test
  void testCreateDealFailsWithExpiredDate() {
    DiscountDealRequestDTO dto = new DiscountDealRequestDTO(Set.of(1L), DiscountType.PERCENTAGE,
        BigDecimal.valueOf(20), 0, 0, LocalDateTime.now().minusDays(1));

    when(productRepository.findAllById(dto.productIds())).thenReturn(List.of(product));

    assertThrows(InvalidDealException.class, () -> discountDealService.createDiscountDeal(dto));
  }

  @Test
  void testGetActiveDealsForProduct() {
    DiscountDeal deal = new DiscountDeal();
    deal.setId(1L);
    deal.setType(DiscountType.PERCENTAGE);
    deal.setExpiresAt(LocalDateTime.now().plusDays(1));

    when(discountDealRepository.findActiveDealsByProductId(eq(1L), any()))
        .thenReturn(List.of(deal));

    var result = discountDealService.getActiveDealsForProduct(1L);

    assertEquals(1, result.size());
    assertEquals(1L, result.get(0).id());
  }
}
