package com.sdex.activityrunner.intent;

import android.content.Intent;
import android.net.Uri;
import com.sdex.activityrunner.intent.param.Category;
import com.sdex.activityrunner.intent.param.Flag;
import java.util.List;

public class LaunchParamsIntentConverter {

  private final LaunchParams launchParams;

  public LaunchParamsIntentConverter(LaunchParams launchParams) {
    this.launchParams = launchParams;
  }

  public Intent convert() {
    Intent intent = new Intent();
    final String packageName = launchParams.getPackageName();
    intent.setPackage(packageName);
    final String className = launchParams.getClassName();
    if (packageName != null && className != null) {
      intent.setClassName(packageName, className);
    }
    intent.setAction(launchParams.getActionValue());
    final String data = launchParams.getData();
    if (data != null) {
      intent.setData(Uri.parse(data));
    }
    final String mimeType = launchParams.getMimeTypeValue();
    intent.setType(mimeType);
    final List<String> categories = Category.list(launchParams.getCategoriesValues());
    for (String category : categories) {
      intent.addCategory(category);
    }
    final List<Integer> flags = Flag.list(launchParams.getFlagsValues());
    for (Integer flag : flags) {
      intent.addFlags(flag);
    }
    return intent;
  }
}
