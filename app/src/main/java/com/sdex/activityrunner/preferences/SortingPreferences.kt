package com.sdex.activityrunner.preferences

import android.content.SharedPreferences
import com.sdex.activityrunner.db.cache.ApplicationModel

class SortingPreferences(private val sharedPreferences: SharedPreferences) {

  val sortBy: String
    get() {
      val sortBy = sharedPreferences.getString(KEY_SORT_BY, KEY_SORT_BY_DEFAULT)
      val position = Integer.parseInt(sortBy ?: KEY_SORT_BY_DEFAULT)
      if (position == 0) {
        return ApplicationModel.NAME
      } else if (position == 1) {
        return ApplicationModel.PACKAGE_NAME
      }
      throw IllegalStateException("Unknown sort by position $position")
    }

  val sortOrder: String
    get() {
      val sortOrder = sharedPreferences.getString(KEY_SORT_ORDER, KEY_SORT_ORDER_DEFAULT)
      val position = Integer.parseInt(sortOrder ?: KEY_SORT_ORDER_DEFAULT)
      if (position == 0) {
        return "ASC"
      } else if (position == 1) {
        return "DESC"
      }
      throw IllegalStateException("Unknown sort order position $position")
    }

  val sortCaseSensitive: String
    get() {
      val caseSensitive = sharedPreferences.getBoolean(KEY_SORT_CASE_SENSITIVE,
        KEY_SORT_CASE_SENSITIVE_DEFAULT)
      return if (!caseSensitive) {
        "COLLATE NOCASE"
      } else ""
    }

  companion object {

    const val KEY_SORT_ORDER = "sort_order"
    const val KEY_SORT_ORDER_DEFAULT = "0"
    const val KEY_SORT_BY = "sort_by"
    const val KEY_SORT_BY_DEFAULT = "0"
    const val KEY_SORT_CASE_SENSITIVE = "sort_case_sensitive"
    const val KEY_SORT_CASE_SENSITIVE_DEFAULT = false
  }
}
