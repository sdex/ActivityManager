package com.sdex.activityrunner.util;

import android.content.ComponentName;
import android.os.AsyncTask;
import android.util.Log;

import com.topjohnwu.superuser.Shell;

public class RunActivityTask extends AsyncTask<Void, Void, Integer> {

  private static final String TAG = "RunActivityTask";
  private final ComponentName componentName;

  public RunActivityTask(ComponentName componentName) {
    this.componentName = componentName;
  }

  @Override
  protected Integer doInBackground(Void[] params) {
    String className = componentName.getClassName();
    if (className.contains("$")) {
      className = className.replace("$", "\\$");
    }

    Log.d(TAG, "class name: " + className);
    try {
      final String command = "am start -n " + componentName.getPackageName() + "/" + className;
      Shell.Sync.su(command);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }
}