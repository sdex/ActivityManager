package com.sdex.activityrunner.info;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.PackageManagerCache;
import com.sdex.activityrunner.util.Utils;
import java.util.Arrays;

public class MyPackageInfo implements Comparable<MyPackageInfo> {

  private String packageName;
  private Bitmap icon;
  private int iconResource;
  private String iconResourceName;
  private String name;
  private MyActivityInfo[] activities;

  public MyPackageInfo(PackageInfo info, PackageManager pm, PackageManagerCache cache) {
    this.packageName = info.packageName;
    ApplicationInfo app = info.applicationInfo;

    if (app != null) {
      this.name = pm.getApplicationLabel(app).toString();
      try {
        this.icon = Utils.getBitmap(pm.getApplicationIcon(app));
      } catch (ClassCastException e) {
        this.icon = Utils.getBitmap(pm.getDefaultActivityIcon());
      }
      this.iconResource = app.icon;
    } else {
      this.name = info.packageName;
      this.icon = Utils.getBitmap(pm.getDefaultActivityIcon());
      this.iconResource = 0;
    }

    this.iconResourceName = null;
    if (this.iconResource != 0) {
      try {
        this.iconResourceName = pm.getResourcesForApplication(app)
          .getResourceName(this.iconResource);
      } catch (Exception e) {
      }
    }

    if (info.activities == null) {
      this.activities = new MyActivityInfo[0];
    } else {
      int n_activities = countActivitiesFromInfo(info);
      int i = 0;

      this.activities = new MyActivityInfo[n_activities];

      for (ActivityInfo activity : info.activities) {
        if (activity.isEnabled() && activity.exported) {
          ComponentName acomp = new ComponentName(activity.packageName, activity.name);
          this.activities[i++] = cache.getActivityInfo(acomp);
        }
      }

      Arrays.sort(this.activities);
    }
  }

  private static int countActivitiesFromInfo(PackageInfo info) {
    int n_activities = 0;
    for (ActivityInfo activity : info.activities) {
      if (activity.isEnabled() && activity.exported) {
        n_activities++;
      }
    }
    return n_activities;
  }

  public int getActivitiesCount() {
    return activities.length;
  }

  public MyActivityInfo getActivity(int i) {
    return activities[i];
  }

  public String getPackageName() {
    return packageName;
  }

  public Bitmap getIcon() {
    return icon;
  }

  public int getIconResource() {
    return iconResource;
  }

  public String getName() {
    return name;
  }

  public String getIconResourceName() {
    return iconResourceName;
  }

  @Override
  public int compareTo(@NonNull MyPackageInfo another) {
    int cmp_name = this.name.compareTo(another.name);
    if (cmp_name != 0) {
      return cmp_name;
    }

    return this.packageName.compareTo(another.packageName);
  }

  @Override
  public boolean equals(Object other) {
    if (!other.getClass().equals(MyPackageInfo.class)) {
      return false;
    }
    MyPackageInfo other_info = (MyPackageInfo) other;
    return this.packageName.equals(other_info.packageName);
  }
}
