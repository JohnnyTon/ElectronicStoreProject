package net.electronicstore.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReceiptDTO(String customerId, List<ReceiptItemDTO> items, BigDecimal total) {
}
