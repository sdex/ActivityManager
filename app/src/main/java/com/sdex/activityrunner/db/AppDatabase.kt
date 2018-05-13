package com.sdex.activityrunner.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.sdex.activityrunner.db.application.ApplicationModel
import com.sdex.activityrunner.db.application.ApplicationModelDao
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryModelDao

@Database(entities = [(ApplicationModel::class), (HistoryModel::class)],
  version = 6,
  exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

  abstract val applicationModelDao: ApplicationModelDao

  abstract val historyModelDao: HistoryModelDao

  companion object {

    private const val DB_NAME = "applications.db"
    private var appDatabase: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      if (appDatabase == null) {
        appDatabase = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
          .fallbackToDestructiveMigration()
          .build()
      }
      return appDatabase!!
    }
  }
}
