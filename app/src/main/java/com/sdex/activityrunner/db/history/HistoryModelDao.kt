package com.sdex.activityrunner.db.history

import androidx.paging.DataSource
import androidx.room.*

@Dao
interface HistoryModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg model: HistoryModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg models: HistoryModel)

    @Delete
    fun delete(vararg models: HistoryModel)

    @Query("SELECT * FROM HistoryModel ORDER BY id DESC")
    fun getHistory(): DataSource.Factory<Int, HistoryModel>

    @Query("DELETE FROM HistoryModel")
    fun clean()
}
