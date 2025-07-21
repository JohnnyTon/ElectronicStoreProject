package net.electronicstore.client.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.electronicstore.client.service.BasketService;
import net.electronicstore.client.test.ElectronicStoreClientTestApplication;
import net.electronicstore.common.dto.BasketLineDTO;
import net.electronicstore.common.dto.ReceiptDTO;
import net.electronicstore.common.dto.ReceiptItemDTO;
import net.electronicstore.common.dto.request.BasketItemRequestDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.enums.OperationStatus;

@SpringBootTest(classes = ElectronicStoreClientTestApplication.class)
@AutoConfigureMockMvc
class BasketControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BasketService basketService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testAddToBasketSuccess() throws Exception {
    UUID customerId = UUID.randomUUID();
    Long productId = 1L;
    int quantity = 2;

    BasketItemRequestDTO request =
        new BasketItemRequestDTO(productId, quantity, customerId.toString());

    BasketLineDTO item = new BasketLineDTO(productId, "Test Product", quantity, BigDecimal.TEN);
    BasketResponseDTO responseDTO =
        new BasketResponseDTO(customerId.toString(), Collections.singletonList(item));

    when(basketService.addToBasket(any(BasketItemRequestDTO.class))).thenReturn(responseDTO);

    mockMvc
        .perform(post("/basket/items").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(OperationStatus.SUCCESS.name()))
        .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.data.items[0].productId").value(productId))
        .andExpect(jsonPath("$.data.items[0].quantity").value(quantity))
        .andExpect(jsonPath("$.data.items[0].price").value(10));
  }

  @Test
  void removeFromBasket_shouldReturnSuccess() throws Exception {
    // Arrange
    UUID customerId = UUID.randomUUID();
    long productId = 1L;

    BasketItemRequestDTO requestDTO = new BasketItemRequestDTO(productId, 1, customerId.toString());

    Product product = new Product();
    product.setId(productId);
    product.setName("Test Product");

    BasketResponseDTO responseDTO =
        new BasketResponseDTO(customerId.toString(), Collections.emptyList());

    when(basketService.removeFromBasket(Mockito.any(BasketItemRequestDTO.class)))
        .thenReturn(responseDTO);

    // Act + Assert
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/basket/items").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("SUCCESS"))
        .andExpect(jsonPath("$.message").value("Item ID " + productId + " removed from basket"))
        .andExpect(jsonPath("$.data.customerId").value(customerId.toString()));
  }

  @Test
  void getBasket_shouldReturnBasketWithItems() throws Exception {
    // Arrange
    UUID customerId = UUID.randomUUID();
    BasketLineDTO item = new BasketLineDTO(1L, "Product 1", 2, BigDecimal.valueOf(9.99));
    BasketResponseDTO responseDTO = new BasketResponseDTO(customerId.toString(), List.of(item));

    when(basketService.getBasket(customerId.toString())).thenReturn(responseDTO);

    // Act + Assert
    mockMvc.perform(get("/basket/{customerId}", customerId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("SUCCESS"))
        .andExpect(jsonPath("$.message").value("Retrieved items in basket"))
        .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.data.items[0].productId").value(1L))
        .andExpect(jsonPath("$.data.items[0].name").value("Product 1"))
        .andExpect(jsonPath("$.data.items[0].quantity").value(2))
        .andExpect(jsonPath("$.data.items[0].price").value(9.99));
  }

  @Test
  void checkout_shouldReturnReceipt() throws Exception {
    // Arrange
    UUID customerId = UUID.randomUUID();
    ReceiptItemDTO item = new ReceiptItemDTO(1L, "Product A", 3, new BigDecimal("19.99"),
        new BigDecimal("59.97"), "10% OFF");
    ReceiptDTO receipt =
        new ReceiptDTO(customerId.toString(), List.of(item), new BigDecimal("59.97"));

    when(basketService.checkout(customerId.toString())).thenReturn(receipt);

    // Act & Assert
    mockMvc
        .perform(get("/basket/{customerId}/checkout", customerId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("SUCCESS"))
        .andExpect(jsonPath("$.message").value("Completed checkout"))
        .andExpect(jsonPath("$.data.customerId").value(customerId.toString()))
        .andExpect(jsonPath("$.data.total").value(59.97))
        .andExpect(jsonPath("$.data.items[0].productId").value(1))
        .andExpect(jsonPath("$.data.items[0].name").value("Product A"))
        .andExpect(jsonPath("$.data.items[0].quantity").value(3))
        .andExpect(jsonPath("$.data.items[0].unitPrice").value(19.99))
        .andExpect(jsonPath("$.data.items[0].subtotal").value(59.97))
        .andExpect(jsonPath("$.data.items[0].appliedDeal").value("10% OFF"));
  }
}
