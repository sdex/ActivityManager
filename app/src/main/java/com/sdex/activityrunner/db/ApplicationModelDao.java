package com.sdex.activityrunner.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;
import java.util.List;

@Dao
public interface ApplicationModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(ApplicationModel... model);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(ApplicationModel... models);

  @Delete
  void delete(ApplicationModel... models);

  @Transaction
  @Query("SELECT * FROM ApplicationModel ORDER BY name")
  LiveData<List<ItemModel>> getAllApplicationModels();
}
