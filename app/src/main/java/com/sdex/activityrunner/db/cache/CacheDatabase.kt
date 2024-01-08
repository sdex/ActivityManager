package com.sdex.activityrunner.db.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [(ApplicationModel::class)],
    version = 6,
    exportSchema = true
)
abstract class CacheDatabase : RoomDatabase() {

    abstract val applicationDao: ApplicationModelDao

    companion object {

        private const val DB_NAME = "cache.db"
        private var database: CacheDatabase? = null

        fun getDatabase(context: Context): CacheDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context, CacheDatabase::class.java, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return database!!
        }
    }
}
