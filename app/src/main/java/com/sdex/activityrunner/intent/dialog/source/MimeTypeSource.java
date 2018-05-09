package com.sdex.activityrunner.intent.dialog.source;

import android.os.Parcel;

import com.sdex.activityrunner.intent.param.MimeType;

import java.util.ArrayList;

public class MimeTypeSource implements SelectionDialogSource {

  @Override
  public ArrayList<String> getList() {
    return MimeType.list();
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

  public MimeTypeSource() {
  }

  protected MimeTypeSource(Parcel in) {
  }

  public static final Creator<MimeTypeSource> CREATOR = new Creator<MimeTypeSource>() {
    @Override
    public MimeTypeSource createFromParcel(Parcel source) {
      return new MimeTypeSource(source);
    }

    @Override
    public MimeTypeSource[] newArray(int size) {
      return new MimeTypeSource[size];
    }
  };
}
