package com.sdex.activityrunner;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.application.ApplicationModelDao;
import com.sdex.activityrunner.db.application.ItemModel;
import java.util.List;

public class ApplicationListViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;
  private final ApplicationModelDao applicationModelDao;

  public ApplicationListViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    applicationModelDao = appDatabase.getApplicationModelDao();
  }

  public LiveData<List<ItemModel>> getItems() {
    return applicationModelDao.getAllApplicationModels();
  }

  public LiveData<List<ItemModel>> getItems(String text) {
    return applicationModelDao.getApplicationModels(text);
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
