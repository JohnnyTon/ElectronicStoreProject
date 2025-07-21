package net.electronicstore.common.exception;

public class ProductNotFoundException extends RuntimeException {
  private static final long serialVersionUID = 7190625201179360789L;

  public ProductNotFoundException(Long id) {
    super("Product not found with ID: " + id);
  }
}
