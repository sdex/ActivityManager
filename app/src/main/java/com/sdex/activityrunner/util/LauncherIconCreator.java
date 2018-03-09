package com.sdex.activityrunner.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.widget.Toast;
import com.sdex.activityrunner.R;
import com.sdex.activityrunner.db.ActivityModel;

public class LauncherIconCreator {

  private static Intent getActivityIntent(ComponentName activity) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setComponent(activity);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    return intent;
  }

  public static void createLauncherIcon(Context context, ActivityModel activityModel) {
    final IconCompat iconCompat = //IconCompat.createWithBitmap(activityModel.getIcon()); // TODO icon
      IconCompat.createWithResource(context, R.mipmap.ic_launcher);
    ShortcutInfoCompat pinShortcutInfo =
      new ShortcutInfoCompat.Builder(context, activityModel.getName())
        .setIcon(iconCompat)
        .setShortLabel(activityModel.getName())
        .setIntent(getActivityIntent(activityModel.getComponentName()))
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