package net.electronicstore.client.exception;

public class OutOfStockException extends RuntimeException {
  private static final long serialVersionUID = 4254552526275812850L;

  public OutOfStockException(String message) {
    super(message);
  }
}
