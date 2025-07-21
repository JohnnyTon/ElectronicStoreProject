package net.electronicstore.common.dto.response;

import java.util.List;

public record PageResponseDTO<T>(List<T> content, int pageNumber, int pageSize, long totalElements,
    int totalPages) {
}
