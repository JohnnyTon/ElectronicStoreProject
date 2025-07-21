package net.electronicstore.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import net.electronicstore.admin.exception.InvalidDealException;
import net.electronicstore.common.dto.response.BaseResponseDTO;
import net.electronicstore.common.entity.DiscountDeal;
import net.electronicstore.common.entity.Product;
import net.electronicstore.common.enums.OperationStatus;
import net.electronicstore.common.exception.ProductNotFoundException;

@Slf4j
@RestControllerAdvice(basePackages = "net.electronicstore.admin.controller")
public class AdminExceptionHandler {

  @ExceptionHandler(InvalidDealException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public BaseResponseDTO<DiscountDeal> handleInvalidDeal(InvalidDealException ex) {
    log.warn("InvalidDealException caught: {}", ex.getMessage(), ex);
    return new BaseResponseDTO<>(null, OperationStatus.FAILED, ex.getMessage());
  }

  @ExceptionHandler(ProductNotFoundException.class)
  @ResponseStatus(value = HttpStatus.NOT_FOUND)
  public BaseResponseDTO<Product> handleProductNotFound(ProductNotFoundException ex) {
    log.warn("ProductNotFoundException caught: {}", ex.getMessage(), ex);
    return new BaseResponseDTO<>(null, OperationStatus.FAILED, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public BaseResponseDTO<Object> handleAllOthers(Exception ex) {
    log.error("Unexpected exception caught: {}", ex.getMessage(), ex);
    return new BaseResponseDTO<>(null, OperationStatus.FAILED,
        "Something went wrong: " + ex.getMessage());
  }
}
