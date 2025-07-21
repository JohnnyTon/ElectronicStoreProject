package net.electronicstore.common.dto.request;

public record BasketItemRequestDTO(Long productId, int quantity, String customerId) {
}
