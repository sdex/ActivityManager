package com.sdex.activityrunner.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.application.ApplicationModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AppLoaderIntentService extends JobIntentService {

  private static final String TAG = "AppLoaderIntentService";

  static final int JOB_ID = 1212;

  public static final String ARG_REASON = "arg_reason";

  public static final int REFRESH_AUTO = 10;
  public static final int REFRESH_USER = 20;

  private static final long FORCE_REFRESH_PERIOD = TimeUnit.HOURS.toMillis(6);

  private static final String PREFERENCES_NAME = "preferences";
  private static final String PREFERENCES_KEY_LAST_UPDATE = "last_update";

  private SharedPreferences preferences;

  public static void enqueueWork(Context context, @NonNull Intent work) {
    enqueueWork(context, AppLoaderIntentService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@Nullable Intent intent) {
    preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    final AppDatabase database = AppDatabase.Companion.getDatabase(this);
    int action = REFRESH_AUTO;
    if (intent != null) {
      action = intent.getIntExtra(ARG_REASON, REFRESH_AUTO);
    }
    if (action == REFRESH_AUTO) {
      final boolean isDatabaseEmpty = database.getApplicationModelDao().count() == 0;
      if (isDatabaseEmpty || needForceRefresh()) {
        updateApplications();
      }
    } else {
      updateApplications();
    }
  }

  private void updateApplications() {
    long start = System.currentTimeMillis();
    final AppDatabase database = AppDatabase.Companion.getDatabase(this);
    PackageManager pm = getPackageManager();

    List<ApplicationModel> applications = new ArrayList<>();

    List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
    for (PackageInfo installedPackage : installedPackages) {
      final String packageName = installedPackage.packageName;
      addInfo(pm, applications, packageName);
    }

    if (installedPackages.isEmpty()) {
      Set<String> packages = new HashSet<>();
      Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
      List<ResolveInfo> ril = pm.queryIntentActivities(intentToResolve, 0);
      for (ResolveInfo resolveInfo : ril) {
        packages.add(resolveInfo.activityInfo.applicationInfo.packageName);
      }
      for (String packageName : packages) {
        addInfo(pm, applications, packageName);
      }
    }

    final ApplicationModel[] applicationsArray = applications.toArray(new ApplicationModel[0]);

    database.getApplicationModelDao().clean();

    database.getApplicationModelDao().insert(applicationsArray);

    saveLastUpdateTime();
    Log.d(TAG, "updateApplications: " + (System.currentTimeMillis() - start) + " ms");
  }

  private void addInfo(PackageManager pm, List<ApplicationModel> applications, String packageName) {
    try {
      PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
      ApplicationModel model = getApplicationModel(pm, packageName, info);
      applications.add(model);
      if (info.activities != null) {
        model.setActivitiesCount(info.activities.length);
        model.setExportedActivitiesCount(getExportedActivitiesCount(info.activities));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void saveLastUpdateTime() {
    preferences.edit()
      .putLong(PREFERENCES_KEY_LAST_UPDATE, System.currentTimeMillis())
      .apply();
  }

  @NonNull
  private ApplicationModel getApplicationModel(PackageManager pm, String packageName,
                                               PackageInfo info) {
    final ApplicationInfo applicationInfo = info.applicationInfo;
    final String name;
    if (applicationInfo != null) {
      name = pm.getApplicationLabel(applicationInfo).toString();
    } else {
      name = info.packageName;
    }
    return new ApplicationModel(name, packageName);
  }

  private boolean needForceRefresh() {
    final long now = System.currentTimeMillis();
    final long lastUpdate = preferences.getLong(PREFERENCES_KEY_LAST_UPDATE, now);
    return now - lastUpdate > FORCE_REFRESH_PERIOD;
  }

  private static int getExportedActivitiesCount(@NonNull ActivityInfo[] activities) {
    int count = 0;
    for (ActivityInfo activity : activities) {
      if (activity.isEnabled() && activity.exported) {
        count++;
      }
    }
    return count;
  }
}
