package com.sdex.activityrunner.intent.history;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.sdex.activityrunner.db.AppDatabase;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.commons.ads.AppPreferences;
import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

  private final AppDatabase database;
  private final AppPreferences appPreferences;

  public HistoryViewModel(@NonNull Application application) {
    super(application);
    database = AppDatabase.getDatabase(application);
    appPreferences = new AppPreferences(application);
  }

  public LiveData<List<HistoryModel>> getHistory() {
    int limit = appPreferences.isProVersion() ? Integer.MAX_VALUE : 20;
    return database.getHistoryRecordDao().getHistory(limit);
  }

  public void deleteItem(HistoryModel model) {
    new DeleteTask(database).execute(model);
  }

  public void clear() {
    new ClearTask(database).execute();
  }

  private static class DeleteTask extends AsyncTask<HistoryModel, Void, Void> {

    private AppDatabase database;

    DeleteTask(AppDatabase appDatabase) {
      database = appDatabase;
    }

    @Override
    protected Void doInBackground(HistoryModel... params) {
      database.getHistoryRecordDao().delete(params);
      return null;
    }
  }

  private static class ClearTask extends AsyncTask<Void, Void, Void> {

    private AppDatabase database;

    ClearTask(AppDatabase appDatabase) {
      database = appDatabase;
    }

    @Override
    protected Void doInBackground(Void... params) {
      database.getHistoryRecordDao().clean();
      return null;
    }
  }
}
