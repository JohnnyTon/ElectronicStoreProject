package net.electronicstore.common.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import net.electronicstore.common.enums.DiscountType;

public record DiscountDealResponseDTO(Long id, DiscountType type, BigDecimal discountPercent,
    Integer buyQuantity, Integer getQuantity, LocalDateTime expiresAt, Set<Long> productIds) {
}
