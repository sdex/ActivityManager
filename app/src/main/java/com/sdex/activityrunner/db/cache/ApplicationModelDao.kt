package com.sdex.activityrunner.db.cache

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface ApplicationModelDao {

    @Upsert
    suspend fun upsert(models: List<ApplicationModel>)

    @Delete
    suspend fun delete(models: List<ApplicationModel>): Int

    @RawQuery(observedEntities = [(ApplicationModel::class)])
    fun getApplicationModels(
        query: SupportSQLiteQuery,
    ): LiveData<List<ApplicationModel>>

    @Query(
        "SELECT * FROM " + ApplicationModel.TABLE +
            " WHERE " + ApplicationModel.PACKAGE_NAME +
            " IN (:packages)",
    )
    suspend fun getApplicationModels(
        packages: Set<String>,
    ): List<ApplicationModel>

    @Query("SELECT * FROM " + ApplicationModel.TABLE)
    suspend fun getApplicationModels(): List<ApplicationModel>

    @Query(
        "SELECT * FROM " + ApplicationModel.TABLE +
            " WHERE " + ApplicationModel.PACKAGE_NAME + " = :packageName",
    )
    suspend fun getApplicationModel(
        packageName: String,
    ): ApplicationModel?

    @Query("SELECT COUNT(*) FROM " + ApplicationModel.TABLE)
    suspend fun count(): Int

    @Query("DELETE FROM " + ApplicationModel.TABLE)
    suspend fun clean()
}
