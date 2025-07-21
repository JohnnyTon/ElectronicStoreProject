package net.electronicstore.common.dto;

import java.math.BigDecimal;

public record BasketLineDTO(Long productId, String name, int quantity, BigDecimal price) {
}
