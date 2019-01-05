package com.sdex.commons.util;

public class Exceptions {

  public static Throwable getRootCause(Throwable exception) {
    Throwable rootException = exception;
    while (rootException.getCause() != null) {
      rootException = rootException.getCause();
    }
    return rootException;
  }
}
