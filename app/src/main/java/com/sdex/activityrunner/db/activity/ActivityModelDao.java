package com.sdex.activityrunner.db.activity;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ActivityModelDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void insert(ActivityModel... model);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void update(ActivityModel... models);

  @Delete
  void delete(ActivityModel... models);

  @Query("SELECT * FROM ActivityModel " +
    "WHERE packageName=:packageName AND exported > :exported " +
    "ORDER BY name")
  LiveData<List<ActivityModel>> getActivityModels(String packageName, int exported);
}
