package net.electronicstore.common.dto.request;

import java.math.BigDecimal;

public record ProductRequestDTO(String name, String description, BigDecimal price, String category,
    Boolean available, Integer stock) {
}
