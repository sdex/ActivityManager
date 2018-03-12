package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.ItemModel;
import java.util.List;

public class ApplicationListViewModel extends AndroidViewModel {

  private AppDatabase appDatabase;
  private final LiveData<List<ItemModel>> appsList;

  public ApplicationListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    appsList = appDatabase.getApplicationModelDao().getAllApplicationModels();
  }

  public LiveData<List<ItemModel>> getItems() {
    return appsList;
  }

//  public void deleteItem(ApplicationModel borrowModel) {
//    new deleteAsyncTask(appDatabase).execute(borrowModel);
//  }

//  private static class deleteAsyncTask extends AsyncTask<ApplicationModel, Void, Void> {
//
//    private AppDatabase db;
//
//    deleteAsyncTask(AppDatabase appDatabase) {
//      db = appDatabase;
//    }
//
//    @Override
//    protected Void doInBackground(final ApplicationModel... params) {
//      db.itemAndPersonModel().deleteBorrow(params[0]);
//      return null;
//    }
//
//  }

}
