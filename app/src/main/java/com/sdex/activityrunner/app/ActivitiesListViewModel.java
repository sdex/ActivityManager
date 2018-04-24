package com.sdex.activityrunner.app;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.preferences.AdvancedPreferences;

import java.util.List;

public class ActivitiesListViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;
  private final AdvancedPreferences advancedPreferences;

  public ActivitiesListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    SharedPreferences sharedPreferences =
      PreferenceManager.getDefaultSharedPreferences(application);
    advancedPreferences = new AdvancedPreferences(sharedPreferences);

  }

  public LiveData<List<ActivityModel>> getItems(String packageName) {
    boolean showNotExported = advancedPreferences.isShowNotExported();
    return appDatabase.getActivityModelDao()
      .getActivityModels(packageName, showNotExported ? -1 : 0 );
  }

}
