package com.sdex.activityrunner.info;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.util.Utils;

@Deprecated
public class MyActivityInfo implements Comparable<MyActivityInfo> {

  private String name;
  private ComponentName componentName;
  private Bitmap icon;

  public MyActivityInfo(ComponentName activity, PackageManager pm) {
    this.componentName = activity;

    ActivityInfo act;
    try {
      act = pm.getActivityInfo(activity, 0);
      this.name = act.loadLabel(pm).toString();
      try {
        this.icon = Utils.getBitmap(act.loadIcon(pm));
      } catch (ClassCastException e) {
        this.icon = Utils.getBitmap(pm.getDefaultActivityIcon());
      }
    } catch (NameNotFoundException e) {
      this.name = activity.getShortClassName();
      this.icon = Utils.getBitmap(pm.getDefaultActivityIcon());
    }
  }

  public ComponentName getComponentName() {
    return componentName;
  }

  public Bitmap getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int compareTo(@NonNull MyActivityInfo another) {
    int cmp_name = this.name.compareTo(another.name);
    if (cmp_name != 0) {
      return cmp_name;
    }
    return this.componentName.compareTo(another.componentName);
  }

  @Override
  public boolean equals(Object other) {
    if (!other.getClass().equals(MyPackageInfo.class)) {
      return false;
    }
    MyActivityInfo other_info = (MyActivityInfo) other;
    return this.componentName.equals(other_info.componentName);
  }
}
