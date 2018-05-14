package com.sdex.activityrunner.db.cache

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [(ApplicationModel::class)],
  version = 1,
  exportSchema = true)
abstract class CacheDatabase : RoomDatabase() {

  abstract val applicationsModelDao: ApplicationModelDao

  companion object {

    private const val DB_NAME = "cache.db"
    private var database: CacheDatabase? = null

    fun getDatabase(context: Context): CacheDatabase {
      if (database == null) {
        database = Room.databaseBuilder(context, CacheDatabase::class.java, DB_NAME)
          .build()
      }
      return database!!
    }
  }
}