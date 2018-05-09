package com.sdex.activityrunner.db.query

import android.text.TextUtils

import com.sdex.activityrunner.db.application.ApplicationModel
import com.sdex.activityrunner.preferences.SortingPreferences

class GetApplicationsQuery(private val searchText: String, sortingPreferences: SortingPreferences) {

  private val sortBy: String = sortingPreferences.sortBy
  private val sortOrder: String = sortingPreferences.sortOrder
  private val sortCaseSensitive: String = sortingPreferences.sortCaseSensitive

  override fun toString(): String {
    val queryStringBuilder = StringBuilder()
    queryStringBuilder.append("SELECT * FROM ").append(ApplicationModel.TABLE).append(" ")
    queryStringBuilder.append("WHERE ").append(ApplicationModel.ACTIVITIES_COUNT).append(" > 0 ")
    if (!TextUtils.isEmpty(searchText)) {
      queryStringBuilder.append(" AND ").append(ApplicationModel.NAME)
        .append(" LIKE '%").append(searchText).append("%' ")
    }
    queryStringBuilder.append("ORDER BY ").append(sortBy).append(" ")
      .append(sortCaseSensitive).append(" ").append(sortOrder)
    return queryStringBuilder.toString()
  }
}
