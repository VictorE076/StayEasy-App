package com.stayeasy.stayeasyspringangular.exception;

public class ForbiddenActionException extends RuntimeException {
  public ForbiddenActionException(String message) {
    super(message);
  }
}
