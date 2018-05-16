package com.sdex.activityrunner.db.cache

import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.*

@Dao
interface ApplicationModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(model: List<ApplicationModel>) : List<Long>

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(models: List<ApplicationModel>) : Int

  @Delete
  fun delete(models: List<ApplicationModel>) : Int

  @RawQuery(observedEntities = [(ApplicationModel::class)])
  fun getApplicationModels(query: SupportSQLiteQuery): LiveData<List<ApplicationModel>>

  @Query("SELECT * FROM " + ApplicationModel.TABLE)
  fun getApplicationModels(): MutableList<ApplicationModel>

  @Query("SELECT COUNT(*) FROM " + ApplicationModel.TABLE)
  fun count(): Int

  @Query("DELETE FROM " + ApplicationModel.TABLE)
  fun clean()
}
