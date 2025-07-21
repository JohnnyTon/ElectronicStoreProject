package net.electronicstore.common.dto;

import java.math.BigDecimal;

public record ReceiptItemDTO(Long productId, String name, int quantity, BigDecimal unitPrice,
    BigDecimal subtotal, String appliedDeal) {
}
