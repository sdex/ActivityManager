package com.sdex.activityrunner;

import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.sdex.activityrunner.info.MyActivityInfo;
import com.sdex.activityrunner.info.MyPackageInfo;
import java.util.HashMap;
import java.util.Map;

public class PackageManagerCache {

  private static PackageManagerCache instance = null;
  private final Map<String, MyPackageInfo> packageInfos;
  private final Map<ComponentName, MyActivityInfo> activityInfos;
  private final PackageManager pm;

  public static PackageManagerCache getPackageManagerCache(PackageManager pm) {
    if (instance == null) {
      instance = new PackageManagerCache(pm);
    }
    return instance;
  }

  private PackageManagerCache(PackageManager pm) {
    this.pm = pm;
    this.packageInfos = new HashMap<>();
    this.activityInfos = new HashMap<>();
  }

  public MyPackageInfo getPackageInfo(String packageName) throws NameNotFoundException {
    MyPackageInfo myInfo;
    synchronized (packageInfos) {
      if (packageInfos.containsKey(packageName)) {
        return packageInfos.get(packageName);
      }
      PackageInfo info;
      info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
      myInfo = new MyPackageInfo(info, pm, this);
      packageInfos.put(packageName, myInfo);
    }
    return myInfo;
  }

  public MyActivityInfo getActivityInfo(ComponentName activityName) {
    MyActivityInfo myInfo;
    synchronized (activityInfos) {
      if (activityInfos.containsKey(activityName)) {
        return activityInfos.get(activityName);
      }
      myInfo = new MyActivityInfo(activityName, pm);
      activityInfos.put(activityName, myInfo);
    }
    return myInfo;
  }
}
