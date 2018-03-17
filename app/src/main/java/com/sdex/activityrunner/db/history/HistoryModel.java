package com.sdex.activityrunner.db.history;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class HistoryModel {

  @PrimaryKey(autoGenerate = true)
  private int id;
  private long timestamp;
  private String packageName;
  private String className;
  private int action;
  private String data;
  private int mimeType;
  private String categories;
  private String flags;

  public HistoryModel() {
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public int getMimeType() {
    return mimeType;
  }

  public void setMimeType(int mimeType) {
    this.mimeType = mimeType;
  }

  public String getCategories() {
    return categories;
  }

  public void setCategories(String categories) {
    this.categories = categories;
  }

  public String getFlags() {
    return flags;
  }

  public void setFlags(String flags) {
    this.flags = flags;
  }
}
