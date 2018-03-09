package com.sdex.activityrunner.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import com.sdex.activityrunner.db.ActivityModel;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.ApplicationModel;
import java.util.ArrayList;
import java.util.List;

public class AppLoaderIntentService extends JobIntentService {

  static final int JOB_ID = 1212;

  public static void enqueueWork(Context context, @NonNull Intent work) {
    enqueueWork(context, AppLoaderIntentService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@Nullable Intent intent) {
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
          final ApplicationInfo app = info.applicationInfo;
          final String name;
          if (app != null) {
            name = pm.getApplicationLabel(app).toString();
          } else {
            name = info.packageName;
          }
          ApplicationModel model = new ApplicationModel(name, packageName);
          applications.add(model);

          for (ActivityInfo activity : info.activities) {
            if (activity.isEnabled() && activity.exported) {
              String activityName;
              try {
                activityName = activity.loadLabel(pm).toString();
              } catch (Exception e) {
                ComponentName componentName = new ComponentName(activity.packageName,
                  activity.name);
                activityName = componentName.getShortClassName();
              }

              ActivityModel activityModel = new ActivityModel(activityName, activity.packageName,
                activity.name);
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
