package net.electronicstore.common.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import net.electronicstore.common.enums.DiscountType;

public record DiscountDealRequestDTO(
    Set<Long> productIds,
    DiscountType type,
    BigDecimal discountPercent,
    Integer buyQuantity,
    Integer getQuantity,
    LocalDateTime expiresAt
) {}
