package com.tec.server.authserver.exception;

public class PermissionDeniedException extends RuntimeException {

  public PermissionDeniedException(String message) {
    super(message);
  }
}
