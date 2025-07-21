package net.electronicstore.client.service;

import net.electronicstore.common.dto.ReceiptDTO;
import net.electronicstore.common.dto.request.BasketItemRequestDTO;
import net.electronicstore.common.dto.response.BasketResponseDTO;

public interface BasketService {
  BasketResponseDTO addToBasket(BasketItemRequestDTO requestDTO);
  BasketResponseDTO removeFromBasket(BasketItemRequestDTO requestDTO);
  BasketResponseDTO getBasket(String customerId);
  ReceiptDTO checkout(String customerId);
}
