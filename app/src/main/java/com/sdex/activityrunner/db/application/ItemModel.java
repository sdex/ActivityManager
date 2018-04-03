package com.sdex.activityrunner.db.application;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.sdex.activityrunner.db.activity.ActivityModel;

import java.util.List;

public class ItemModel {

  @Embedded
  private ApplicationModel applicationModel;
  @Relation(parentColumn = "packageName", entityColumn = "packageName")
  private List<ActivityModel> activityModels;

  public ApplicationModel getApplicationModel() {
    return applicationModel;
  }

  public void setApplicationModel(ApplicationModel applicationModel) {
    this.applicationModel = applicationModel;
  }

  public List<ActivityModel> getActivityModels() {
    return activityModels;
  }

  public void setActivityModels(List<ActivityModel> activityModels) {
    this.activityModels = activityModels;
  }
}
