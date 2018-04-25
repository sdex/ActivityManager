package com.sdex.activityrunner.preferences;

import android.content.SharedPreferences;

import com.sdex.activityrunner.db.application.ApplicationModel;

public class SortingPreferences {

  private final SharedPreferences sharedPreferences;

  public SortingPreferences(SharedPreferences sharedPreferences) {
    this.sharedPreferences = sharedPreferences;
  }

  public String getSortBy() {
    String sortBy = sharedPreferences.getString(SettingsActivity.KEY_SORT_BY,
      SettingsActivity.KEY_SORT_BY_DEFAULT);
    int position = Integer.parseInt(sortBy);
    if (position == 0) {
      return ApplicationModel.NAME;
    } else if (position == 1) {
      return ApplicationModel.PACKAGE_NAME;
    }
    throw new IllegalStateException("Unknown sort by position " + position);
  }

  public String getSortOrder() {
    String sortOrder = sharedPreferences.getString(SettingsActivity.KEY_SORT_ORDER,
      SettingsActivity.KEY_SORT_ORDER_DEFAULT);
    int position = Integer.parseInt(sortOrder);
    if (position == 0) {
      return "ASC";
    } else if (position == 1) {
      return "DESC";
    }
    throw new IllegalStateException("Unknown sort order position " + position);
  }

  public String getSortCaseSensitive() {
    boolean caseSensitive = sharedPreferences.getBoolean(SettingsActivity.KEY_SORT_CASE_SENSITIVE,
      SettingsActivity.KEY_SORT_CASE_SENSITIVE_DEFAULT);
    if (!caseSensitive) {
      return "COLLATE NOCASE";
    }
    return "";
  }
}
