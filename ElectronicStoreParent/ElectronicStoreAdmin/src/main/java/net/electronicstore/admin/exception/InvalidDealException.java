package net.electronicstore.admin.exception;

public class InvalidDealException extends RuntimeException {

  private static final long serialVersionUID = 266039162961472241L;

  public InvalidDealException(String message) {
    super(message);
  }
}
