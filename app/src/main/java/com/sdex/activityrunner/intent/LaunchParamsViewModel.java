package com.sdex.activityrunner.intent;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter;

public class LaunchParamsViewModel extends AndroidViewModel {

  private final AppDatabase appDatabase;

  public LaunchParamsViewModel(@NonNull Application application) {
    super(application);
    appDatabase = AppDatabase.getDatabase(application);
  }

  public void addToHistory(LaunchParams launchParams) {
    new InsertAsyncTask(appDatabase).execute(launchParams);
  }

  private static class InsertAsyncTask extends AsyncTask<LaunchParams, Void, Void> {

    private final AppDatabase database;

    InsertAsyncTask(AppDatabase database) {
      this.database = database;
    }

    @Override
    protected Void doInBackground(final LaunchParams... params) {
      LaunchParamsToHistoryConverter historyConverter =
        new LaunchParamsToHistoryConverter(params[0]);
      final HistoryModel historyModel = historyConverter.convert();
      database.getHistoryModelDao().insert(historyModel);
      return null;
    }

  }

}
