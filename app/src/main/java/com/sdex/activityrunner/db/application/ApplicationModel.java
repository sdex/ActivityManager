package com.sdex.activityrunner.db.application;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"packageName"})
public class ApplicationModel {

  @NonNull
  private String name;
  @NonNull
  private String packageName;
  @NonNull
  private String iconPath;

  public ApplicationModel(@NonNull String name, @NonNull String packageName,
    @NonNull String iconPath) {
    this.name = name;
    this.packageName = packageName;
    this.iconPath = iconPath;
  }

  @NonNull
  public String getName() {
    return name;
  }

  @NonNull
  public String getPackageName() {
    return packageName;
  }

  @NonNull
  public String getIconPath() {
    return iconPath;
  }
}
