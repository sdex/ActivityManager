package com.sdex.activityrunner.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sdex.activityrunner.BuildConfig;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.glide.GlideApp;
import com.sdex.activityrunner.shortcut.ShortcutHandlerActivity;

public class IntentUtils {

  private static Intent getActivityIntent(ComponentName activity) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setComponent(activity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    return intent;
  }

  public static void createLauncherIcon(Context context, ActivityModel activityModel,
                                        Bitmap bitmap) {
    ComponentName componentName;
    if (activityModel.isExported()) {
      componentName = activityModel.getComponentName();
    } else {
      componentName = new ComponentName(BuildConfig.APPLICATION_ID,
        ShortcutHandlerActivity.class.getCanonicalName());
    }

    Intent intent = getActivityIntent(componentName);

    if (!activityModel.isExported()) {
      ComponentName originComponent = activityModel.getComponentName();
      intent.putExtra(ShortcutHandlerActivity.ARG_PACKAGE_NAME, originComponent.getPackageName());
      intent.putExtra(ShortcutHandlerActivity.ARG_CLASS_NAME, originComponent.getClassName());
    }

    IconCompat iconCompat = IconCompat.createWithBitmap(bitmap);
    createLauncherIcon(context, activityModel.getName(), intent, iconCompat);
  }

  public static void createLauncherIcon(Context context, String name, Intent intent,
                                        @DrawableRes int icon) {
    final IconCompat iconCompat = IconCompat.createWithResource(context, icon);
    createLauncherIcon(context, name, intent, iconCompat);
  }

  private static void createLauncherIcon(Context context, String name, Intent intent,
                                         IconCompat iconCompat) {
    ShortcutInfoCompat pinShortcutInfo = new ShortcutInfoCompat.Builder(context, name)
      .setIcon(iconCompat)
      .setShortLabel(name)
      .setIntent(intent)
      .build();
    ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null);
  }

  public static void createLauncherIcon(final Context context, final ActivityModel activityModel) {
    GlideApp.with(context)
      .asDrawable()
      .load(activityModel)
      .error(R.mipmap.ic_launcher)
      .override(100)
      .into(new SimpleTarget<Drawable>() {
        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
          createLauncherIcon(context, activityModel, Utils.getBitmap(resource));
        }
      });
  }

  public static void launchActivity(Context context, ComponentName activity, String name) {
    try {
      Intent intent = getActivityIntent(activity);
      context.startActivity(intent);
      Toast.makeText(context, context.getString(R.string.starting_activity, name),
        Toast.LENGTH_SHORT).show();
    } catch (SecurityException e) {
      Toast.makeText(context, context.getString(R.string.starting_activity_failed_security, name),
        Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      Toast.makeText(context, context.getString(R.string.starting_activity_failed, name),
        Toast.LENGTH_SHORT).show();
    }
  }

  public static void launchActivity(Context context, Intent intent) {
    try {
      context.startActivity(intent);
      Toast.makeText(context, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      new AlertDialog.Builder(context)
        .setTitle(R.string.starting_activity_intent_failed)
        .setMessage(e.getMessage())
        .setPositiveButton(android.R.string.ok, null)
        .show();
    }
  }

  public static void openApplicationInfo(Context context, String packageName) {
    try {
      Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      intent.setData(Uri.parse("package:" + packageName));
      context.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
      context.startActivity(intent);
    }
  }
}