package com.sdex.activityrunner.db.application

import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.*

@Dao
interface ApplicationModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg model: ApplicationModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg models: ApplicationModel)

    @Delete
    fun delete(vararg models: ApplicationModel)

    @Transaction
    @RawQuery(observedEntities = arrayOf(ApplicationModel::class))
    fun getApplicationModels(query: SupportSQLiteQuery): LiveData<List<ApplicationModel>>

    @Query("SELECT COUNT(*) FROM ApplicationModel")
    fun count(): Int

    @Query("DELETE FROM ApplicationModel")
    fun clean()
}
