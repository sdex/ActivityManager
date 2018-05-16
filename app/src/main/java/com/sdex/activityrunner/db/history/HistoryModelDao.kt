package com.sdex.activityrunner.db.history

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface HistoryModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(vararg model: HistoryModel)

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(vararg models: HistoryModel)

  @Delete
  fun delete(vararg models: HistoryModel)

  @Query("SELECT * FROM HistoryModel ORDER BY id DESC LIMIT :limit")
  fun getHistory(limit: Int): LiveData<List<HistoryModel>>

  @Query("DELETE FROM HistoryModel")
  fun clean()
}
