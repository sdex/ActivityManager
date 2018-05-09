package com.sdex.activityrunner.intent.dialog.source;

import android.os.Parcel;

import com.sdex.activityrunner.intent.param.Category;

import java.util.ArrayList;

public class CategoriesSource implements SelectionDialogSource {

  @Override
  public ArrayList<String> getList() {
    return Category.list();
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

  public CategoriesSource() {
  }

  protected CategoriesSource(Parcel in) {
  }

  public static final Creator<CategoriesSource> CREATOR = new Creator<CategoriesSource>() {
    @Override
    public CategoriesSource createFromParcel(Parcel source) {
      return new CategoriesSource(source);
    }

    @Override
    public CategoriesSource[] newArray(int size) {
      return new CategoriesSource[size];
    }
  };
}
