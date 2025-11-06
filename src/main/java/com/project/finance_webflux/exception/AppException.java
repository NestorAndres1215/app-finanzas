package com.project.finance_webflux.exception;

public class AppException extends RuntimeException {
  public AppException(String message) {
    super(message);
  }
}
