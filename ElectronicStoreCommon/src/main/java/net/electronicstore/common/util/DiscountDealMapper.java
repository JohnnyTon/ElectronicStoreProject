package net.electronicstore.common.util;

import java.util.Set;
import java.util.stream.Collectors;
import net.electronicstore.common.dto.response.DiscountDealResponseDTO;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;

public class DiscountDealMapper {

  public static DiscountDealResponseDTO toResponse(DiscountDeal deal) {
    Set<Long> productIds =
        deal.getProducts().stream().map(Product::getId).collect(Collectors.toSet());

    return new DiscountDealResponseDTO(deal.getId(), deal.getType(),
        deal.getDiscountPercent(), deal.getBuyQuantity(), deal.getGetQuantity(),
        deal.getExpiresAt(), productIds);
  }
}

