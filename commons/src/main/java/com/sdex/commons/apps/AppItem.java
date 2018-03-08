package com.sdex.commons.apps;

public class AppItem {

  private final String icon;
  private final String name;
  private final String description;
  private final String packageName;

  public AppItem(String name, String description, String packageName, String icon) {
    this.name = name;
    this.description = description;
    this.packageName = packageName;
    this.icon = icon;
  }

  public String getIcon() {
    return icon;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getPackageName() {
    return packageName;
  }
}
