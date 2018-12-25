package com.sdex.activityrunner.db.cache.query

import com.sdex.activityrunner.db.cache.ApplicationModel

class GetApplicationsQuery(private val searchText: String?) {

  private val sortBy: String = ApplicationModel.NAME
  private val sortOrder: String = "ASC"
  private val sortCaseSensitive: String = "COLLATE NOCASE"

  override fun toString(): String {
    val queryStringBuilder = StringBuilder()
    queryStringBuilder.append("SELECT * FROM ").append(ApplicationModel.TABLE).append(" ")
    queryStringBuilder.append("WHERE ")
      .append(ApplicationModel.ACTIVITIES_COUNT).append(" > 0 ")
    if (!searchText.isNullOrEmpty()) {
      val escapedSearchText = searchText.replace("'", "''")
      queryStringBuilder.append("AND (")
        .append(ApplicationModel.NAME)
        .append(" LIKE '%").append(escapedSearchText).append("%' ")
        .append("OR ")
        .append(ApplicationModel.PACKAGE_NAME)
        .append(" LIKE '%").append(escapedSearchText).append("%' ")
        .append(") ")
    }
    queryStringBuilder.append("ORDER BY ").append(sortBy).append(" ")
      .append(sortCaseSensitive).append(" ").append(sortOrder)
    return queryStringBuilder.toString()
  }
}
