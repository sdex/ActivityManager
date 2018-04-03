package com.sdex.activityrunner.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sdex.activityrunner.db.activity.ActivityModel;
import com.sdex.activityrunner.db.activity.ActivityModelDao;
import com.sdex.activityrunner.db.application.ApplicationModel;
import com.sdex.activityrunner.db.application.ApplicationModelDao;
import com.sdex.activityrunner.db.history.HistoryModel;
import com.sdex.activityrunner.db.history.HistoryModelDao;

@Database(entities = {ApplicationModel.class, ActivityModel.class, HistoryModel.class},
  version = 5,
  exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

  private static final String DB_NAME = "applications.db";
  private static AppDatabase appDatabase;

  public static AppDatabase getDatabase(Context context) {
    if (appDatabase == null) {
      appDatabase = Room.databaseBuilder(context, AppDatabase.class, DB_NAME)
        .fallbackToDestructiveMigration()
        .build();
    }
    return appDatabase;
  }

  public abstract ApplicationModelDao getApplicationModelDao();

  public abstract ActivityModelDao getActivityModelDao();

  public abstract HistoryModelDao getHistoryModelDao();
}
