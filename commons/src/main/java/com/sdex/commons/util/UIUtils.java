package com.sdex.commons.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public final class UIUtils {

  public static Drawable getDrawable(String name, Context context) {
    Resources resources = context.getResources();
    final int resourceId = resources.getIdentifier(name, "drawable",
      context.getPackageName());
    return ContextCompat.getDrawable(context, resourceId);
  }

}
