package com.sdex.activityrunner.util;

public class ObjectsCompat {

  /**
   * see {@link java.util.Objects#requireNonNull(Object)}
   */
  public static <T> T requireNonNull(T obj) {
    if (obj == null) {
      throw new NullPointerException();
    }
    return obj;
  }

}
