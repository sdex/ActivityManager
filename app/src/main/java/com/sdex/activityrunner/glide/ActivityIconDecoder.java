package com.sdex.activityrunner.glide;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.util.Util;
import com.sdex.activityrunner.db.activity.ActivityModel;

class ActivityIconDecoder implements ResourceDecoder<ActivityModel, Drawable> {

  private final Context context;

  public ActivityIconDecoder(Context context) {
    this.context = context;
  }

  @Nullable
  @Override
  public Resource<Drawable> decode(@NonNull ActivityModel source, int width, int height,
                                   @NonNull Options options) {
    PackageManager packageManager = context.getPackageManager();
    Drawable icon;
    try {
      Intent intent = new Intent();
      intent.setComponent(source.getComponentName());
      ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
      icon = resolveInfo.loadIcon(packageManager);
    } catch (Exception e) {
      icon = packageManager.getDefaultActivityIcon();
    }
    return new DrawableResource<Drawable>(icon) {
      @NonNull
      @Override
      public Class<Drawable> getResourceClass() {
        return Drawable.class;
      }

      @Override
      public int getSize() { // best effort
        if (drawable instanceof BitmapDrawable) {
          return Util.getBitmapByteSize(((BitmapDrawable) drawable).getBitmap());
        } else {
          return 1;
        }
      }

      @Override
      public void recycle() { /* not from our pool */ }
    };
  }

  @Override
  public boolean handles(@NonNull ActivityModel source, @NonNull Options options) {
    return true;
  }
}