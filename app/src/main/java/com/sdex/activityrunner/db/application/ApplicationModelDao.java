package com.sdex.activityrunner.db.application;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteQuery;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RawQuery;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.sdex.activityrunner.db.activity.ActivityModel;

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
  @RawQuery(observedEntities = {ApplicationModel.class, ActivityModel.class})
  LiveData<List<ItemModel>> getApplicationModels(SupportSQLiteQuery query);

  @Query("SELECT COUNT(*) FROM ApplicationModel")
  int count();

  @Query("DELETE FROM ApplicationModel")
  void clean();
}
