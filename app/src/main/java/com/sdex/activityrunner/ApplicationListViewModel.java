package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.db.application.ItemModel;
import java.util.List;

public class ApplicationListViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;
  private final SharedPreferences sharedPreferences;

  public ApplicationListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
  }

  private String getSortBy() {
    String sortByValue = sharedPreferences.getString(SettingsActivity.KEY_SORT_BY,
      SettingsActivity.KEY_SORT_BY_DEFAULT);
    int position = Integer.parseInt(sortByValue);
    if (position == 0) {
      return ApplicationModel.NAME;
    } else if (position == 1) {
      return ApplicationModel.PACKAGE_NAME;
    }
    throw new IllegalStateException("Unknown sort by position " + position);
  }

  public LiveData<List<ItemModel>> getItems() {
    String sortBy = "name";
    return appDatabase.getApplicationModelDao().getAllApplicationModels(sortBy);
  }

  public LiveData<List<ItemModel>> getItems(String text) {
    String sortBy = getSortBy();
    return appDatabase.getApplicationModelDao().getApplicationModels(text);
  }
}
