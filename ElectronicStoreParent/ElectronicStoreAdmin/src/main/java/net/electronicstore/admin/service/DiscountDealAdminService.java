package net.electronicstore.admin.service;

import java.util.List;
import net.electronicstore.common.dto.request.DiscountDealRequestDTO;
import net.electronicstore.common.dto.response.DiscountDealResponseDTO;

public interface DiscountDealAdminService {
  DiscountDealResponseDTO createDiscountDeal(DiscountDealRequestDTO dto);

  List<DiscountDealResponseDTO> getActiveDealsForProduct(Long productId);
}
