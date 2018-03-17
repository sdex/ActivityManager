package com.sdex.activityrunner.intent;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.sdex.activityrunner.intent.param.Action;
import com.sdex.activityrunner.intent.param.Category;
import com.sdex.activityrunner.intent.param.Flag;
import java.util.ArrayList;
import java.util.List;

public class LaunchParamsIntentConverter implements LaunchParamsConverter<Intent> {

  private static final String TAG = "LaunchParamsIntent";

  private final LaunchParams launchParams;

  public LaunchParamsIntentConverter(LaunchParams launchParams) {
    this.launchParams = launchParams;
  }

  @Override
  public Intent convert() {
    Intent intent = new Intent();
    final String packageName = launchParams.getPackageName();
    intent.setPackage(packageName);
    final String className = launchParams.getClassName();
    if (packageName != null && className != null) {
      intent.setClassName(packageName, className);
    }
    intent.setAction(Action.getAction(launchParams.getActionValue()));
    final String data = launchParams.getData();
    if (data != null) {
      intent.setData(Uri.parse(data));
    }
    intent.setType(launchParams.getMimeTypeValue());
    final List<String> categories = Category.list(launchParams.getCategoriesValues());
    for (String category : categories) {
      intent.addCategory(category);
    }
    final List<Integer> flags = Flag.list(launchParams.getFlagsValues());
    for (Integer flag : flags) {
      intent.addFlags(flag);
    }
    final ArrayList<LaunchParamsExtra> extras = launchParams.getExtras();
    addExtras(intent, extras);
    return intent;
  }

  private void addExtras(Intent intent, ArrayList<LaunchParamsExtra> extras) {
    for (LaunchParamsExtra extra : extras) {
      final int type = extra.getType();
      final String key = extra.getKey();
      final String value = extra.getValue();
      try {
        switch (type) {
          case LaunchParamsExtraType.STRING:
            intent.putExtra(key, value);
            break;
          case LaunchParamsExtraType.INT:
            intent.putExtra(key, Integer.parseInt(value));
            break;
          case LaunchParamsExtraType.LONG:
            intent.putExtra(key, Long.parseLong(value));
            break;
          case LaunchParamsExtraType.FLOAT:
            intent.putExtra(key, Float.parseFloat(value));
            break;
          case LaunchParamsExtraType.DOUBLE:
            intent.putExtra(key, Double.parseDouble(value));
            break;
          case LaunchParamsExtraType.BOOLEAN:
            intent.putExtra(key, Boolean.parseBoolean(value));
            break;
        }
      } catch (NumberFormatException e) {
        Log.d(TAG, "Failed to parse number");
      }
    }
  }
}
