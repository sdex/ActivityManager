package com.sdex.activityrunner.db.cache

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ApplicationModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(models: List<ApplicationModel>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(models: List<ApplicationModel>): Int

    @Delete
    fun delete(models: List<ApplicationModel>): Int

    @RawQuery(observedEntities = [(ApplicationModel::class)])
    fun getApplicationModels(query: SupportSQLiteQuery): LiveData<List<ApplicationModel>>

    @Query("SELECT * FROM " + ApplicationModel.TABLE)
    fun getApplicationModels(): List<ApplicationModel>

    @Query("SELECT * FROM " + ApplicationModel.TABLE + " WHERE " + ApplicationModel.PACKAGE_NAME + " = :packageName")
    fun getApplicationModel(packageName: String): ApplicationModel?

    @Query("SELECT COUNT(*) FROM " + ApplicationModel.TABLE)
    fun count(): Int

    @Query("DELETE FROM " + ApplicationModel.TABLE)
    fun clean()
}
