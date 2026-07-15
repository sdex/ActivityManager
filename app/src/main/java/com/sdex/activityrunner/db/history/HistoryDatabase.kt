package com.sdex.activityrunner.db.history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [(HistoryModel::class)],
    version = 2,
    exportSchema = true,
)
abstract class HistoryDatabase : RoomDatabase() {

    abstract val historyDao: HistoryModelDao

    companion object {

        private const val DB_NAME = "history.db"
        private var database: HistoryDatabase? = null

        fun getDatabase(context: Context): HistoryDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context, HistoryDatabase::class.java, DB_NAME)
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return database!!
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE HistoryModel ADD COLUMN useRoot INTEGER NOT NULL DEFAULT 0",
                )
            }
        }
    }
}
