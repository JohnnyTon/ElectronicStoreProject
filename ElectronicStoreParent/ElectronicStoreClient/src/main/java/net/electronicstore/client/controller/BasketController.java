package net.electronicstore.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.electronicstore.client.service.BasketService;
import net.electronicstore.common.dto.ReceiptDTO;
import net.electronicstore.common.dto.request.BasketItemRequestDTO;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;
import net.electronicstore.common.enums.OperationStatus;

@Slf4j
@RestController
@RequestMapping("/basket")
public class BasketController {

  private final BasketService basketService;

  public BasketController(BasketService basketService) {
    this.basketService = basketService;
  }

  @PostMapping("/items")
  public ResponseEntity<BaseResponseDTO<BasketResponseDTO>> addToBasket(
      @RequestBody BasketItemRequestDTO request) {
    BasketResponseDTO response = basketService.addToBasket(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new BaseResponseDTO<>(response, OperationStatus.SUCCESS, "Item added to basket"));
  }

  @DeleteMapping("/items")
  public ResponseEntity<BaseResponseDTO<BasketResponseDTO>> removeFromBasket(
      @RequestBody BasketItemRequestDTO request) {
    BasketResponseDTO response = basketService.removeFromBasket(request);
    return ResponseEntity.ok(new BaseResponseDTO<>(response, OperationStatus.SUCCESS,
        "Item ID " + request.productId() + " removed from basket"));
  }

  @GetMapping("/{customerId}")
  public ResponseEntity<BaseResponseDTO<BasketResponseDTO>> getBasket(@PathVariable String customerId) {
    BasketResponseDTO response = basketService.getBasket(customerId);
    log.debug("Basket contents for {}: {}", customerId, response);
    return ResponseEntity
        .ok(new BaseResponseDTO<>(response, OperationStatus.SUCCESS, "Retrieved items in basket"));
  }

  @GetMapping("/{customerId}/checkout")
  public ResponseEntity<BaseResponseDTO<ReceiptDTO>> checkout(@PathVariable String customerId) {
    log.info("Performing checkout for customerId: {}", customerId);
    ReceiptDTO response = basketService.checkout(customerId);
    log.debug("Checkout result for {}: {}", customerId, response);
    return ResponseEntity
        .ok(new BaseResponseDTO<>(response, OperationStatus.SUCCESS, "Completed checkout"));
  }
}
