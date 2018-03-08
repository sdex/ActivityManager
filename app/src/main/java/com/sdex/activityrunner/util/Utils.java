package com.sdex.activityrunner.util;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;

public class Utils {

  public static Bitmap getBitmap(@NonNull Drawable drawable) {
    if (drawable instanceof BitmapDrawable) {
      return ((BitmapDrawable) drawable).getBitmap();
    } else {
      return getBitmapFromDrawable(drawable);
    }
  }

  private static Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      if (drawable instanceof AdaptiveIconDrawable) {
        return getBitmapFromAdaptiveDrawable((AdaptiveIconDrawable) drawable);
      }
    }
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      if (drawable instanceof VectorDrawable) {
        return getBitmapFromVectorDrawable((VectorDrawable) drawable);
      }
    }
    return null;
  }

  @TargetApi(Build.VERSION_CODES.O)
  private static Bitmap getBitmapFromAdaptiveDrawable(@NonNull AdaptiveIconDrawable drawable) {
    Drawable[] drr = new Drawable[2];
    drr[0] = drawable.getBackground();
    drr[1] = drawable.getForeground();

    LayerDrawable layerDrawable = new LayerDrawable(drr);

    int width = layerDrawable.getIntrinsicWidth();
    int height = layerDrawable.getIntrinsicHeight();

    Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);

    layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    layerDrawable.draw(canvas);

    bitmap = getBitmapClippedCircle(bitmap);

    return bitmap;
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  private static Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
    Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
      vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    vectorDrawable.draw(canvas);
    return bitmap;
  }

  private static Bitmap getBitmapClippedCircle(Bitmap bitmap) {
    final int width = bitmap.getWidth();
    final int height = bitmap.getHeight();
    final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    final Path path = new Path();
    path.addCircle((float) (width / 2), (float) (height / 2),
      (float) Math.min(width, (height / 2)), Path.Direction.CCW);
    final Canvas canvas = new Canvas(outputBitmap);
    canvas.clipPath(path);
    canvas.drawBitmap(bitmap, 0, 0, null);
    return outputBitmap;
  }

}
