package com.itccompliance.oi.domain.error;

public class InsufficientStockException extends RuntimeException {
  public InsufficientStockException(String message) { super(message); }
}
