package net.electronicstore.admin.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.electronicstore.admin.service.DiscountDealAdminService;
import net.electronicstore.common.dto.request.DiscountDealRequestDTO;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.dto.response.DiscountDealResponseDTO;
import net.electronicstore.common.enums.OperationStatus;

@Slf4j
@RestController
@RequestMapping("/deals")
public class DiscountDealAdminController {

  private final DiscountDealAdminService discountDealAdminService;

  public DiscountDealAdminController(DiscountDealAdminService discountDealAdminService) {
    this.discountDealAdminService = discountDealAdminService;
  }

  @PostMapping
  public ResponseEntity<BaseResponseDTO<DiscountDealResponseDTO>> createDiscountDeal(
      @RequestBody DiscountDealRequestDTO dto) {
    log.info("Received request to create discount deal: {}", dto);
    DiscountDealResponseDTO response = discountDealAdminService.createDiscountDeal(dto);
    log.debug("Created discount deal response: {}", response);
    return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponseDTO<>(response,
        OperationStatus.SUCCESS, "Discount deal created successfully"));
  }

  @GetMapping
  public ResponseEntity<BaseResponseDTO<List<DiscountDealResponseDTO>>> getActiveDealsForProduct(
      @RequestParam Long productId) {
    log.info("Fetching active deals for productId: {}", productId);
    List<DiscountDealResponseDTO> response =
        discountDealAdminService.getActiveDealsForProduct(productId);
    log.debug("Active deals found: {}", response);
    return ResponseEntity.ok(new BaseResponseDTO<>(response, OperationStatus.SUCCESS,
        "Retrieved active deals for product ID " + productId));
  }
}
