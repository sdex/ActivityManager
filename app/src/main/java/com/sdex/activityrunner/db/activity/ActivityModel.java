package com.sdex.activityrunner.db.activity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.content.ComponentName;
import android.support.annotation.NonNull;

import com.sdex.activityrunner.db.application.ApplicationModel;

import java.io.Serializable;

@Entity(primaryKeys = {"className"},
  indices = {@Index(value = {"packageName"})},
  foreignKeys = @ForeignKey(entity = ApplicationModel.class,
    parentColumns = "packageName",
    childColumns = "packageName",
    onDelete = ForeignKey.CASCADE))
public class ActivityModel implements Serializable {

  @NonNull
  private String name;
  @NonNull
  private String packageName;
  @NonNull
  private String className;
  @NonNull
  private String iconPath;
  @NonNull
  private boolean exported;

  public ActivityModel(@NonNull String name, @NonNull String packageName,
    @NonNull String className, @NonNull String iconPath, @NonNull boolean exported) {
    this.name = name;
    this.packageName = packageName;
    this.className = className;
    this.iconPath = iconPath;
    this.exported = exported;
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

  @NonNull
  public String getIconPath() {
    return iconPath;
  }

  public void setIconPath(@NonNull String iconPath) {
    this.iconPath = iconPath;
  }

  @NonNull
  public boolean isExported() {
    return exported;
  }

  public void setExported(@NonNull boolean exported) {
    this.exported = exported;
  }

  public ComponentName getComponentName() {
    return new ComponentName(packageName, className);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ActivityModel that = (ActivityModel) o;

    if (!packageName.equals(that.packageName)) return false;
    return className.equals(that.className);
  }

  @Override
  public int hashCode() {
    int result = packageName.hashCode();
    result = 31 * result + className.hashCode();
    return result;
  }
}
