package com.sdex.activityrunner.intent;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter;
import com.sdex.commons.ads.AppPreferences;

public class LaunchParamsViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;
  private final AppPreferences appPreferences;

  public LaunchParamsViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
    appPreferences = new AppPreferences(application);
  }

  public void addToHistory(LaunchParams launchParams) {
    boolean deleteOldRecords = false;
    if (!appPreferences.isProVersion()) {
      deleteOldRecords = true;
    }
    new InsertAsyncTask(appDatabase, deleteOldRecords).execute(launchParams);
  }

  private static class InsertAsyncTask extends AsyncTask<LaunchParams, Void, Void> {

    private final AppDatabase database;
    private final boolean deleteOldRecords;

    InsertAsyncTask(AppDatabase database, boolean deleteOldRecords) {
      this.database = database;
      this.deleteOldRecords = deleteOldRecords;
    }

    @Override
    protected Void doInBackground(final LaunchParams... params) {
      LaunchParamsToHistoryConverter historyConverter =
        new LaunchParamsToHistoryConverter(params[0]);
      final HistoryModel historyModel = historyConverter.convert();
      database.getHistoryRecordDao().insert(historyModel);
      if (deleteOldRecords) {
        // TODO delete old records
      }
      return null;
    }

  }

}
