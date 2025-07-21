package net.electronicstore.common.util;

import net.electronicstore.common.dto.BasketLineDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;
import net.electronicstore.common.entity.Basket;

public class BasketMapper {

  public static BasketResponseDTO toDto(Basket basket) {
    return new BasketResponseDTO(
        basket.getCustomerId().toString(),
        basket.getItems().stream()
            .map(item -> new BasketLineDTO(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice()
            ))
            .toList()
    );
  }
}
