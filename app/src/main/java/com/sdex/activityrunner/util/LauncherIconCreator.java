/**
 * Based on code from Stackoverflow.com under CC BY-SA 3.0
 * Url: http://stackoverflow.com/questions/6493518/create-a-shortcut-for-any-app-on-desktop
 * By:  http://stackoverflow.com/users/815400/xuso
 * <p>
 * and
 * <p>
 * Url: http://stackoverflow.com/questions/3298908/creating-a-shortcut-how-can-i-work-with-a-drawable-as-icon
 * By:  http://stackoverflow.com/users/327402/waza-be
 */

package com.sdex.activityrunner.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.Toast;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.info.MyActivityInfo;

public class LauncherIconCreator {

  private static Intent getActivityIntent(ComponentName activity) {
    Intent intent = new Intent();
    intent.setComponent(activity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    return intent;
  }

  public static void createLauncherIcon(Context context, MyActivityInfo activity) {
    final String pack = activity.getIconResourceName()
      .substring(0, activity.getIconResourceName().indexOf(':'));

    // Use bitmap version if icon from different package is used
    if (!pack.equals(activity.getComponentName().getPackageName())) {
      createLauncherIcon(context, activity.getComponentName(), activity.getName(),
        activity.getIcon());
    } else {
      createLauncherIcon(context, activity.getComponentName(), activity.getName(),
        activity.getIconResourceName());
    }
  }

  public static void createLauncherIcon(Context context, ComponentName activity, String name,
    Bitmap icon) {
    Toast.makeText(context,
      String.format(context.getText(R.string.creating_activity_shortcut).toString(), name),
      Toast.LENGTH_LONG).show();

    Intent shortcutIntent = new Intent();
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getActivityIntent(activity));
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
    shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    context.sendBroadcast(shortcutIntent);
    //finish();
  }

  public static void createLauncherIcon(Context context, ComponentName activity, String name,
    String icon_resource_name) {
    Toast.makeText(context,
      String.format(context.getText(R.string.creating_activity_shortcut).toString(), name),
      Toast.LENGTH_LONG).show();

    Intent shortcutIntent = new Intent();
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getActivityIntent(activity));
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
    Intent.ShortcutIconResource ir = new Intent.ShortcutIconResource();
    ir.packageName = activity.getPackageName();
    ir.resourceName = icon_resource_name;
    shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, ir);
    shortcutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
    context.sendBroadcast(shortcutIntent);
    //finish();
  }

  public static void launchActivity(Context context, ComponentName activity, String name) {
    Intent intent = LauncherIconCreator.getActivityIntent(activity);
    Toast.makeText(context,
      String.format(context.getText(R.string.starting_activity).toString(), name),
      Toast.LENGTH_LONG).show();
    context.startActivity(intent);
  }
}