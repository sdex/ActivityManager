package com.sdex.activityrunner.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryModelDao

@Database(entities = [(HistoryModel::class)],
  version = 1,
  exportSchema = true)
abstract class HistoryDatabase : RoomDatabase() {

  abstract val historyModelDao: HistoryModelDao

  companion object {

    private const val DB_NAME = "history.db"
    private var historyDatabase: HistoryDatabase? = null

    fun getDatabase(context: Context): HistoryDatabase {
      if (historyDatabase == null) {
        historyDatabase = Room.databaseBuilder(context, HistoryDatabase::class.java, DB_NAME)
          .build()
      }
      return historyDatabase!!
    }
  }
}
