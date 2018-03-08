package com.sdex.commons.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetUtils {

  public static boolean isConnected(Context context) {
    ConnectivityManager manager = (ConnectivityManager) context
      .getSystemService(Context.CONNECTIVITY_SERVICE);
    return manager != null && manager.getActiveNetworkInfo() != null &&
      manager.getActiveNetworkInfo().isAvailable() && manager.getActiveNetworkInfo().isConnected();
  }
}
