package com.sdex.activityrunner.db.history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg model: HistoryModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg models: HistoryModel)

    @Delete
    fun delete(vararg models: HistoryModel)

    @Query("SELECT * FROM HistoryModel ORDER BY id DESC")
    fun getHistory(): LiveData<List<HistoryModel>>

    @Query("DELETE FROM HistoryModel")
    fun clean()
}
