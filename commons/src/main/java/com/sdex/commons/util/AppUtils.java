package com.sdex.commons.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class AppUtils {

  public static final String DEV_PAGE = "https://play.google.com/store/apps/dev?id=8437279387942631019";

  public static void openLink(Context context, String url) {
    try {
      context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    } catch (Exception e) {
      Toast.makeText(context, "Failed to open link", Toast.LENGTH_SHORT).show();
    }
  }

  public static void openApp(Context context, String appPackageName) {
    if (isAppInstalled(context, appPackageName)) {
      try {
        Intent LaunchIntent = context.getPackageManager()
          .getLaunchIntentForPackage(appPackageName);
        context.startActivity(LaunchIntent);
        return;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    openPlayStore(context, appPackageName);
  }

  public static void openPlayStore(Context context) {
    openPlayStore(context, context.getPackageName());
  }

  public static void openPlayStore(Context context, String appPackageName) {
    try {
      context.startActivity(
        new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
    } catch (ActivityNotFoundException anfe) {
      try {
        context.startActivity(new Intent(Intent.ACTION_VIEW,
          Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
      } catch (Exception e) {
        Toast.makeText(context, "Failed to open Play Store", Toast.LENGTH_SHORT).show();
      }
    }
  }

  private static boolean isAppInstalled(Context context, String appPackageName) {
    PackageManager pm = context.getPackageManager();
    try {
      pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
    }
    return false;
  }

  public static boolean isXposedInstalled(Context context) {
    final String appPackageName = "de.robv.android.xposed.installer";
    PackageManager pm = context.getPackageManager();
    try {
      pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
      return true;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return false;
  }
}
