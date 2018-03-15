package com.sdex.activityrunner.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import java.util.concurrent.TimeUnit;

public class AppLoaderIntentService extends JobIntentService {

  static final int JOB_ID = 1212;

  public static final String ARG_REASON = "arg_reason";

  public static final int REFRESH_AUTO = 10;
  public static final int REFRESH_USER = 20;

  private static final long CLEAN_IMAGE_CACHE_PERIOD = TimeUnit.DAYS.toMillis(30);
  private static final long FORCE_REFRESH_PERIOD = TimeUnit.DAYS.toMillis(7);

  private static final String PREFERENCES_NAME = "preferences";
  private static final String PREFERENCES_KEY_LAST_UPDATE = "last_update";

  private SharedPreferences preferences;

  public static void enqueueWork(Context context, @NonNull Intent work) {
    enqueueWork(context, AppLoaderIntentService.class, JOB_ID, work);
  }

  @Override
  protected void onHandleWork(@Nullable Intent intent) {
    preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
    final AppDatabase database = AppDatabase.getDatabase(this);
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
      if (needCleanImages()) {
        final File imageDir = getImagesDir();
        for (File file : imageDir.listFiles()) {
          //noinspection ResultOfMethodCallIgnored
          file.delete();
        }
      }
      database.getApplicationModelDao().clean();
      updateApplications();
    }
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
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    final ApplicationModel[] applicationsArray = applications
      .toArray(new ApplicationModel[applications.size()]);
    final ActivityModel[] activitiesArray = activities
      .toArray(new ActivityModel[activities.size()]);

    database.getApplicationModelDao().insert(applicationsArray);
    database.getActivityModelDao().insert(activitiesArray);

    saveLastUpdateTime();
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
    File file = new File(getImagesDir(), String.valueOf(id.hashCode()));
    if (!file.exists() && bitmap != null) {
      IOUtils.writeToFile(file, bitmap);
    }
    return file.getAbsolutePath();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private File getImagesDir() {
    final File filesDir = getFilesDir();
    File imagesDir = new File(filesDir, "images");
    if (!imagesDir.exists()) {
      imagesDir.mkdir();
    }
    return imagesDir;
  }

  private boolean needCleanImages() {
    final long now = System.currentTimeMillis();
    final long lastUpdate = preferences.getLong(PREFERENCES_KEY_LAST_UPDATE, now);
    return now - lastUpdate > CLEAN_IMAGE_CACHE_PERIOD;
  }

  private boolean needForceRefresh() {
    final long now = System.currentTimeMillis();
    final long lastUpdate = preferences.getLong(PREFERENCES_KEY_LAST_UPDATE, now);
    return now - lastUpdate > FORCE_REFRESH_PERIOD;
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
