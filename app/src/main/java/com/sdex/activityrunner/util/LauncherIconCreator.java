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
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.widget.Toast;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.info.MyActivityInfo;

public class LauncherIconCreator {

  private static Intent getActivityIntent(ComponentName activity) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setComponent(activity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    return intent;
  }

  public static void createLauncherIcon(Context context, MyActivityInfo activity) {
    final IconCompat iconCompat = IconCompat.createWithBitmap(activity.getIcon());
    ShortcutInfoCompat pinShortcutInfo =
      new ShortcutInfoCompat.Builder(context, activity.getName())
        .setIcon(iconCompat)
        .setShortLabel(activity.getName())
        .setIntent(getActivityIntent(activity.getComponentName()))
        .build();
    ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null);
  }

  public static void launchActivity(Context context, ComponentName activity, String name) {
    Intent intent = LauncherIconCreator.getActivityIntent(activity);
    Toast.makeText(context,
      String.format(context.getText(R.string.starting_activity).toString(), name),
      Toast.LENGTH_LONG).show();
    context.startActivity(intent);
  }
}