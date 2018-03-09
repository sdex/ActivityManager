package com.sdex.commons.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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

  public static void writeToFile(File file, Bitmap bitmap) {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @SuppressWarnings("EmptyCatchBlock")
  public static File writeToFile(File directory, String filename, InputStream inputStream)
    throws IOException {
    FileOutputStream fileOutput = null;
    try {
      File file = new File(directory, filename);
      byte[] buffer = new byte[1024];
      int bufferLength;
      fileOutput = new FileOutputStream(file);
      while ((bufferLength = inputStream.read(buffer)) > 0) {
        fileOutput.write(buffer, 0, bufferLength);
      }
      Log.d(TAG, "Image downloaded at: " + file.getAbsolutePath());
      fileOutput.close();
      return file;
    } catch (IOException e) {
      Log.e(TAG, "Error saving the content path", e);
    } finally {
      if (fileOutput != null) {
        fileOutput.close();
      }
      if (inputStream != null) {
        inputStream.close();
      }
    }
    return null;
  }

}
