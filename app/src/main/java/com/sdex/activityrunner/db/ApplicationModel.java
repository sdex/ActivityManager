package com.sdex.activityrunner.db;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"packageName"})
public class ApplicationModel {

  private String name;
  @NonNull
  private String packageName;

  public ApplicationModel(String name, @NonNull String packageName) {
    this.name = name;
    this.packageName = packageName;
  }

  public String getName() {
    return name;
  }

  @NonNull
  public String getPackageName() {
    return packageName;
  }
}
