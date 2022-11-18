package com.sdex.activityrunner.db.cache.query

import androidx.sqlite.db.SimpleSQLiteQuery
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.preferences.AppPreferences

class GetApplicationsQuery(
    private val appPreferences: AppPreferences,
    private val searchText: String? = null,
) {

    private val sortBy = ApplicationModel.NAME
    private val sortOrder = "ASC"
    private val sortCaseSensitive = "COLLATE NOCASE"

    val sqLiteQuery get() = SimpleSQLiteQuery(toString())

    override fun toString(): String {
        val queryStringBuilder = StringBuilder()
        queryStringBuilder.append("SELECT * FROM ").append(ApplicationModel.TABLE).append(" ")
            .append("WHERE (")
            .append(ApplicationModel.ACTIVITIES_COUNT).append(" >0")
        if (appPreferences.isShowDisabledApps) {
            queryStringBuilder.append(" OR ")
                .append(ApplicationModel.ENABLED).append(" =0")
        }
        queryStringBuilder.append(") ")
        if (!appPreferences.isShowSystemApps) {
            queryStringBuilder.append("AND (")
                .append(ApplicationModel.SYSTEM).append(" =0")
                .append(") ")
        }
        if (!searchText.isNullOrEmpty()) {
            val escapedSearchText = searchText.replace("'", "''")
            queryStringBuilder.append("AND (")
                .append(ApplicationModel.NAME)
                .append(" LIKE '%").append(escapedSearchText).append("%'")
                .append(" OR ")
                .append(ApplicationModel.PACKAGE_NAME)
                .append(" LIKE '%").append(escapedSearchText).append("%'")
                .append(") ")
        }
        queryStringBuilder.append("ORDER BY ").append(sortBy).append(" ")
            .append(sortCaseSensitive).append(" ").append(sortOrder)
        return queryStringBuilder.toString()
    }
}
