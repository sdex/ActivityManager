package com.sdex.activityrunner.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.content.ComponentName;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"className"},
  indices = {@Index(value = {"packageName"})},
  foreignKeys = @ForeignKey(entity = ApplicationModel.class,
    parentColumns = "packageName",
    childColumns = "packageName",
    onDelete = ForeignKey.CASCADE))
public class ActivityModel {

  @NonNull
  private String name;
  @NonNull
  private String packageName;
  @NonNull
  private String className;

  public ActivityModel(@NonNull String name, @NonNull String packageName,
    @NonNull String className) {
    this.name = name;
    this.packageName = packageName;
    this.className = className;
  }

  public ActivityModel(@NonNull String name, @NonNull ComponentName componentName) {
    this.name = name;
    this.packageName = componentName.getPackageName();
    this.className = componentName.getClassName();
  }

  @NonNull
  public String getName() {
    return name;
  }

  public void setName(@NonNull String name) {
    this.name = name;
  }

  @NonNull
  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(@NonNull String packageName) {
    this.packageName = packageName;
  }

  @NonNull
  public String getClassName() {
    return className;
  }

  public void setClassName(@NonNull String className) {
    this.className = className;
  }

  public ComponentName getComponentName() {
    return new ComponentName(packageName, className);
  }
}
