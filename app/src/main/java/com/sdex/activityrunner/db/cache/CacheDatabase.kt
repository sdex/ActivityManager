package com.sdex.activityrunner.db.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [(ApplicationModel::class)],
    version = 8,
    exportSchema = true,
)
abstract class CacheDatabase : RoomDatabase() {

    abstract val applicationDao: ApplicationModelDao

    companion object {

        private const val DB_NAME = "cache.db"
        private var database: CacheDatabase? = null

        fun getDatabase(context: Context): CacheDatabase {
            if (database == null) {
                database = Room.databaseBuilder(context, CacheDatabase::class.java, DB_NAME)
                    .addMigrations(MIGRATION_7_8)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
            }
            return database!!
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE ${ApplicationModel.TABLE} " +
                        "ADD COLUMN ${ApplicationModel.PINNED_AT} INTEGER NOT NULL DEFAULT 0",
                )
            }
        }
    }
}
