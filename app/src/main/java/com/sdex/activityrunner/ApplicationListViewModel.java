package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SimpleSQLiteQuery;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.application.ItemModel;
import com.sdex.activityrunner.db.query.GetApplicationsQuery;
import com.sdex.activityrunner.preferences.AdvancedPreferences;
import com.sdex.activityrunner.preferences.SortingPreferences;

import java.util.List;

public class ApplicationListViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;
  private final SortingPreferences sortingPreferences;
  private final AdvancedPreferences advancedPreferences;

  public ApplicationListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    SharedPreferences sharedPreferences =
      PreferenceManager.getDefaultSharedPreferences(application);
    sortingPreferences = new SortingPreferences(sharedPreferences);
    advancedPreferences = new AdvancedPreferences(sharedPreferences);
  }

  public LiveData<List<ItemModel>> getItems(String searchText) {
    GetApplicationsQuery query =
      new GetApplicationsQuery(searchText, sortingPreferences, advancedPreferences);
    return appDatabase.getApplicationModelDao()
      .getApplicationModels(new SimpleSQLiteQuery(query.toString()));
  }

  public LiveData<List<ItemModel>> getItems() {
    return getItems(null);
  }
}
