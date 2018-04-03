package com.sdex.activityrunner.db.application;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"packageName"})
public class ApplicationModel {

  public static final String TABLE = "ApplicationModel";
  public static final String NAME = "name";
  public static final String PACKAGE_NAME = "packageName";
  public static final String ACTIVITIES_COUNT = "activitiesCount";
  public static final String EXPORTED_ACTIVITIES_COUNT = "exportedActivitiesCount";

  @NonNull
  private String name;
  @NonNull
  private String packageName;
  @NonNull
  private String iconPath;
  @NonNull
  private int activitiesCount;
  @NonNull
  private int exportedActivitiesCount;

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

  @NonNull
  public int getActivitiesCount() {
    return activitiesCount;
  }

  public void setActivitiesCount(@NonNull int activitiesCount) {
    this.activitiesCount = activitiesCount;
  }

  @NonNull
  public int getExportedActivitiesCount() {
    return exportedActivitiesCount;
  }

  public void setExportedActivitiesCount(@NonNull int exportedActivitiesCount) {
    this.exportedActivitiesCount = exportedActivitiesCount;
  }
}
