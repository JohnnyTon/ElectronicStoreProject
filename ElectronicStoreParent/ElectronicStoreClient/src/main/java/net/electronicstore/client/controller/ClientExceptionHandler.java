package net.electronicstore.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import net.electronicstore.client.exception.OutOfStockException;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.enums.OperationStatus;

@Slf4j
@RestControllerAdvice(basePackages = "net.electronicstore.client.controller")
public class ClientExceptionHandler {

  @ExceptionHandler(OutOfStockException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<Product> handleOutOfStock(OutOfStockException ex) {
    log.warn("OutOfStockException caught: {}", ex.getMessage(), ex);
    return new BaseResponseDTO<>(null, OperationStatus.FAILED, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public BaseResponseDTO<Object> handleAllOthers(Exception ex) {
    log.error("Unhandled exception caught in client controller: {}", ex.getMessage(), ex);
    return new BaseResponseDTO<>(null, OperationStatus.FAILED,
        "Something went wrong: " + ex.getMessage());
  }
}
