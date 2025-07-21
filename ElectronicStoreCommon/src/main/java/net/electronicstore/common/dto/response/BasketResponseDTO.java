package net.electronicstore.common.dto.response;

import java.util.List;
import net.electronicstore.common.dto.BasketLineDTO;

public record BasketResponseDTO(String customerId, List<BasketLineDTO> items) {
}
