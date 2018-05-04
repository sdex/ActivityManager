package com.sdex.activityrunner.db.activity

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface ActivityModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg model: ActivityModel)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg models: ActivityModel)

  @Delete
  fun delete(vararg models: ActivityModel)

  @Query("SELECT * FROM ActivityModel " +
    "WHERE packageName=:packageName AND exported > :exported " +
    "ORDER BY name")
  fun getActivityModels(packageName: String, exported: Int): LiveData<List<ActivityModel>>
}
