package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.application.ItemModel;
import java.util.List;

public class ApplicationListViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;

  public ApplicationListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
  }

  public LiveData<List<ItemModel>> getItems() {
    return appDatabase.getApplicationModelDao().getAllApplicationModels();
  }

  public LiveData<List<ItemModel>> getItems(String text) {
    return appDatabase.getApplicationModelDao().getApplicationModels(text);
  }
}
