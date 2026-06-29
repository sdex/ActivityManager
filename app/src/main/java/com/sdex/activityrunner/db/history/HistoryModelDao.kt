package com.sdex.activityrunner.db.history

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryModelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg model: HistoryModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(vararg models: HistoryModel)

    @Delete
    suspend fun delete(vararg models: HistoryModel)

    @Query("SELECT * FROM HistoryModel ORDER BY id DESC")
    fun getHistory(): Flow<List<HistoryModel>>

    @Query("DELETE FROM HistoryModel")
    suspend fun clean()
}
