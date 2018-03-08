package com.sdex.activityrunner.info;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.util.Utils;

public class MyActivityInfo implements Comparable<MyActivityInfo> {

  private ComponentName componentName;
  private Bitmap icon;
  private int iconResource;
  private String iconResourceName;
  private String name;

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
      this.iconResource = act.getIconResource();
    } catch (NameNotFoundException e) {
      this.name = activity.getShortClassName();
      this.icon = Utils.getBitmap(pm.getDefaultActivityIcon());
      this.iconResource = 0;
    }

    this.iconResourceName = null;
    if (this.iconResource != 0) {
      try {
        this.iconResourceName = pm.getResourcesForActivity(activity)
          .getResourceName(this.iconResource);
      } catch (Exception e) {
      }
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

  public String getIconResourceName() {
    return iconResourceName;
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
