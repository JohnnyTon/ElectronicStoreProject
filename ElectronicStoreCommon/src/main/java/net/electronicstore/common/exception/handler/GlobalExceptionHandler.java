package net.electronicstore.common.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.enums.OperationStatus;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<Object> handleIllegalArgument(IllegalArgumentException ex) {
    return new BaseResponseDTO<>(null, OperationStatus.FAILED, ex.getMessage());
  }
}
