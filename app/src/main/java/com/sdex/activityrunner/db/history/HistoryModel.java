package com.sdex.activityrunner.db.history;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class HistoryModel {

  @PrimaryKey(autoGenerate = true)
  private int id;

  public HistoryModel() {
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }
}
