package com.sdex.activityrunner.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.ApplicationModel;
import com.sdex.activityrunner.util.Utils;
import com.sdex.commons.util.IOUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppLoaderIntentService extends JobIntentService {

  static final int JOB_ID = 1212;

  public static void enqueueWork(Context context, @NonNull Intent work) {
    enqueueWork(context, AppLoaderIntentService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@Nullable Intent intent) {
    // TODO check intent and do full refresh or partial
    updateApplications();
  }

  private void updateApplications() {
    final AppDatabase database = AppDatabase.getDatabase(this);
    PackageManager pm = getPackageManager();

    List<ApplicationModel> applications = new ArrayList<>();
    List<ActivityModel> activities = new ArrayList<>();

    List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
    for (PackageInfo installedPackage : installedPackages) {
      try {
        final String packageName = installedPackage.packageName;
        PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        if (countActivitiesFromInfo(info) > 0) {
          ApplicationModel model = getApplicationModel(pm, packageName, info);
          applications.add(model);

          for (ActivityInfo activityInfo : info.activities) {
            if (activityInfo.isEnabled() && activityInfo.exported) {
              ActivityModel activityModel = getActivityModel(pm, activityInfo);
              activities.add(activityModel);
            }
          }
        }
      } catch (NameNotFoundException e) {
        e.printStackTrace();
      }
    }

    final ApplicationModel[] applicationsArray = applications
      .toArray(new ApplicationModel[applications.size()]);
    final ActivityModel[] activitiesArray = activities
      .toArray(new ActivityModel[activities.size()]);

    database.getApplicationModelDao().insert(applicationsArray);
    database.getActivityModelDao().insert(activitiesArray);
  }

  @NonNull
  private ApplicationModel getApplicationModel(PackageManager pm, String packageName,
    PackageInfo info) {
    final ApplicationInfo applicationInfo = info.applicationInfo;
    final Bitmap bitmap = getBitmap(pm, applicationInfo);
    final String iconPath = saveIcon(bitmap, packageName);
    final String name;
    if (applicationInfo != null) {
      name = pm.getApplicationLabel(applicationInfo).toString();
    } else {
      name = info.packageName;
    }
    return new ApplicationModel(name, packageName, iconPath);
  }

  @NonNull
  private ActivityModel getActivityModel(PackageManager pm, ActivityInfo activityInfo) {
    String activityName;
    try {
      activityName = activityInfo.loadLabel(pm).toString();
    } catch (Exception e) {
      ComponentName componentName = new ComponentName(activityInfo.packageName, activityInfo.name);
      activityName = componentName.getShortClassName();
    }
    final Bitmap bitmap = getBitmap(pm, activityInfo);
    final String iconPath = saveIcon(bitmap, activityInfo.packageName + activityName);
    return new ActivityModel(activityName, activityInfo.packageName, activityInfo.name, iconPath);
  }

  private Bitmap getBitmap(PackageManager pm, ActivityInfo act) {
    try {
      try {
        return Utils.getBitmap(act.loadIcon(pm));
      } catch (Exception e) {
        return Utils.getBitmap(pm.getDefaultActivityIcon());
      }
    } catch (Exception e) {
      return Utils.getBitmap(pm.getDefaultActivityIcon());
    }
  }

  private Bitmap getBitmap(PackageManager pm, ApplicationInfo app) {
    if (app != null) {
      try {
        return Utils.getBitmap(pm.getApplicationIcon(app));
      } catch (Exception e) {
        return Utils.getBitmap(pm.getDefaultActivityIcon());
      }
    } else {
      return Utils.getBitmap(pm.getDefaultActivityIcon());
    }
  }

  private String saveIcon(Bitmap bitmap, String id) {
    File file = new File(getCacheDir(), String.valueOf(id.hashCode()));
    if (!file.exists()) {
      IOUtils.writeToFile(file, bitmap);
    }
    return file.getAbsolutePath();
  }

  private static int countActivitiesFromInfo(PackageInfo info) {
    int count = 0;
    final ActivityInfo[] activities = info.activities;
    if (activities != null) {
      for (ActivityInfo activity : activities) {
        if (activity.isEnabled() && activity.exported) {
          count++;
        }
      }
    }
    return count;
  }
}
