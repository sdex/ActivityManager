package com.sdex.commons.util;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class IOUtils {

  private static final String TAG = "IOUtils";

  public static String loadAssetTextAsString(Context context, String name) {
    BufferedReader in = null;
    try {
      StringBuilder buf = new StringBuilder();
      InputStream is = context.getAssets().open(name);
      in = new BufferedReader(new InputStreamReader(is));

      String str;
      boolean isFirst = true;
      while ((str = in.readLine()) != null) {
        if (isFirst) {
          isFirst = false;
        } else {
          buf.append('\n');
        }
        buf.append(str);
      }
      return buf.toString();
    } catch (IOException e) {
      Log.e(TAG, "Error opening asset " + name);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
          Log.e(TAG, "Error closing asset " + name);
        }
      }
    }

    return null;
  }

}
