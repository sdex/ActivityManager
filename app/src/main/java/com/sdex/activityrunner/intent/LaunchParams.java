package com.sdex.activityrunner.intent;

import com.sdex.activityrunner.intent.param.Action;
import com.sdex.activityrunner.intent.param.Category;
import com.sdex.activityrunner.intent.param.Flag;
import com.sdex.activityrunner.intent.param.MimeType;
import java.util.ArrayList;

public class LaunchParams {

  private String packageName;
  private String className;
  private int action;
  private String data;
  private int mimeType;
  private ArrayList<Integer> categories = new ArrayList<>(0);
  private ArrayList<Integer> flags = new ArrayList<>(0);

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getActionValue() {
    if (action == 0) {
      return null;
    }
    return Action.list().get(action);
  }

  public int getAction() {
    return action;
  }

  public void setAction(int action) {
    this.action = action;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getMimeTypeValue() {
    if (mimeType == 0) {
      return null;
    }
    return MimeType.list().get(mimeType);
  }

  public int getMimeType() {
    return mimeType;
  }

  public void setMimeType(int mimeType) {
    this.mimeType = mimeType;
  }

  public ArrayList<Integer> getCategories() {
    return categories;
  }

  public ArrayList<String> getCategoriesValues() {
    final ArrayList<String> list = Category.list();
    final ArrayList<String> result = new ArrayList<>(categories.size());
    for (Integer position : categories) {
      result.add(list.get(position));
    }
    return result;
  }

  public void setCategories(ArrayList<Integer> categories) {
    this.categories.clear();
    this.categories.addAll(categories);
  }

  public ArrayList<Integer> getFlags() {
    return flags;
  }

  public ArrayList<String> getFlagsValues() {
    final ArrayList<String> list = Flag.list();
    final ArrayList<String> result = new ArrayList<>(flags.size());
    for (Integer position : flags) {
      result.add(list.get(position));
    }
    return result;
  }

  public void setFlags(ArrayList<Integer> flags) {
    this.flags.clear();
    this.flags.addAll(flags);
  }
}
