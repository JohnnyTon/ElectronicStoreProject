package net.electronicstore.common.dto.response;

import java.math.BigDecimal;

public record ProductResponseDTO(Long id, String name, String description, BigDecimal price,
    String category, boolean available, int stock) {
}

