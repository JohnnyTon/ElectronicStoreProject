package net.electronicstore.common.dto.response;

import net.electronicstore.common.enums.OperationStatus;

public record BaseResponseDTO<T>(T data, OperationStatus status, String message) {
}
