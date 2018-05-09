package com.sdex.activityrunner.intent.dialog.source;

import android.os.Parcel;

import com.sdex.activityrunner.intent.param.Action;

import java.util.ArrayList;

public class ActionSource implements SelectionDialogSource {

  @Override
  public ArrayList<String> getList() {
    return Action.list();
  }

  @Override
  public String getItem(int position) {
    return getList().get(position);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
  }

  public ActionSource() {
  }

  protected ActionSource(Parcel in) {
  }

  public static final Creator<ActionSource> CREATOR = new Creator<ActionSource>() {
    @Override
    public ActionSource createFromParcel(Parcel source) {
      return new ActionSource(source);
    }

    @Override
    public ActionSource[] newArray(int size) {
      return new ActionSource[size];
    }
  };
}
