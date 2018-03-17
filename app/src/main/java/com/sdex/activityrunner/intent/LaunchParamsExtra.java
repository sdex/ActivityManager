package com.sdex.activityrunner.intent;

import android.os.Parcel;
import android.os.Parcelable;

public class LaunchParamsExtra implements Parcelable {

  private String key;
  private String value;
  @LaunchParamsExtraType
  private int type;
  private boolean isArray;

  public LaunchParamsExtra() {
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isArray() {
    return isArray;
  }

  public void setArray(boolean array) {
    isArray = array;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.key);
    dest.writeString(this.value);
    dest.writeInt(this.type);
    dest.writeByte(this.isArray ? (byte) 1 : (byte) 0);
  }

  protected LaunchParamsExtra(Parcel in) {
    this.key = in.readString();
    this.value = in.readString();
    this.type = in.readInt();
    this.isArray = in.readByte() != 0;
  }

  public static final Creator<LaunchParamsExtra> CREATOR = new Creator<LaunchParamsExtra>() {
    @Override
    public LaunchParamsExtra createFromParcel(Parcel source) {
      return new LaunchParamsExtra(source);
    }

    @Override
    public LaunchParamsExtra[] newArray(int size) {
      return new LaunchParamsExtra[size];
    }
  };
}
