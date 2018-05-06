package com.sdex.activityrunner.shortcut;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sdex.activityrunner.util.RunActivityTask;

public class ShortcutHandlerActivity extends Activity {

  public static final String ARG_PACKAGE_NAME = "arg_package_name";
  public static final String ARG_CLASS_NAME = "arg_class_name";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String packageName = getIntent().getStringExtra(ARG_PACKAGE_NAME);
    String className = getIntent().getStringExtra(ARG_CLASS_NAME);
    if (packageName != null && className != null) {
      ComponentName componentName = new ComponentName(packageName, className);
      RunActivityTask task = new RunActivityTask(componentName);
      task.execute();
    }
    finish();
  }
}
