package com.sdex.activityrunner.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {ApplicationModel.class, ActivityModel.class},
  version = 1,
  exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

  private static final String DB_NAME = "application_db";
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
}
