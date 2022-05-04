package com.sdex.activityrunner.db.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [(HistoryModel::class)],
    version = 1,
    exportSchema = true
)
abstract class HistoryDatabase : RoomDatabase() {

    abstract val historyDao: HistoryModelDao

    companion object {

        private const val DB_NAME = "history.db"
        private var database: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context, HistoryDatabase::class.java, DB_NAME)
                    .build()
            }
            return database!!
        }
    }
}
