package com.sdex.activityrunner.db.history;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface HistoryModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(HistoryModel... model);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(HistoryModel... models);

  @Delete
  void delete(HistoryModel... models);

  @Query("SELECT * FROM HistoryModel ORDER BY id DESC")
  LiveData<List<HistoryModel>> getHistory();

  @Query("DELETE FROM HistoryModel")
  void clean();
}
