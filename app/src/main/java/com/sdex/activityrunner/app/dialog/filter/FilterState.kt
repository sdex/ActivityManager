package com.sdex.activityrunner.app.dialog.filter

import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

data class FilterState(
    val sortBy: String = ApplicationModel.Companion.NAME,
    val sortOrder: String = GetApplicationsQuery.Companion.ASC,
    val isShowSystemApps: Boolean = false,
    val isShowSystemAppIndicator: Boolean = false,
    val isShowDisabledApps: Boolean = false,
    val isShowDisabledAppIndicator: Boolean = false,
)
