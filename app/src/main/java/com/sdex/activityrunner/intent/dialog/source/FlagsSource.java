package com.sdex.activityrunner.intent.dialog.source;

import android.os.Parcel;

import com.sdex.activityrunner.intent.param.Flag;

import java.util.ArrayList;

public class FlagsSource implements SelectionDialogSource {

  @Override
  public ArrayList<String> getList() {
    return Flag.list();
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

  public FlagsSource() {
  }

  protected FlagsSource(Parcel in) {
  }

  public static final Creator<FlagsSource> CREATOR = new Creator<FlagsSource>() {
    @Override
    public FlagsSource createFromParcel(Parcel source) {
      return new FlagsSource(source);
    }

    @Override
    public FlagsSource[] newArray(int size) {
      return new FlagsSource[size];
    }
  };
}
