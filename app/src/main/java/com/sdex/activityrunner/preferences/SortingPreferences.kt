package com.sdex.activityrunner.preferences

import android.content.SharedPreferences

import com.sdex.activityrunner.app.ApplicationModel

class SortingPreferences(private val sharedPreferences: SharedPreferences) {

  val sortBy: String
    get() {
      val sortBy = sharedPreferences.getString(SettingsActivity.KEY_SORT_BY,
        SettingsActivity.KEY_SORT_BY_DEFAULT)
      val position = Integer.parseInt(sortBy)
      if (position == 0) {
        return ApplicationModel.NAME
      } else if (position == 1) {
        return ApplicationModel.PACKAGE_NAME
      }
      throw IllegalStateException("Unknown sort by position $position")
    }

  val sortOrder: String
    get() {
      val sortOrder = sharedPreferences.getString(SettingsActivity.KEY_SORT_ORDER,
        SettingsActivity.KEY_SORT_ORDER_DEFAULT)
      val position = Integer.parseInt(sortOrder)
      if (position == 0) {
        return "ASC"
      } else if (position == 1) {
        return "DESC"
      }
      throw IllegalStateException("Unknown sort order position $position")
    }

  val sortCaseSensitive: String
    get() {
      val caseSensitive = sharedPreferences.getBoolean(SettingsActivity.KEY_SORT_CASE_SENSITIVE,
        SettingsActivity.KEY_SORT_CASE_SENSITIVE_DEFAULT)
      return if (!caseSensitive) {
        "COLLATE NOCASE"
      } else ""
    }
}
