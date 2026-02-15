package com.sdex.activityrunner.preferences

import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

data class DisplayConfig(
    val showSystemApps: Boolean = true,
    val showSystemAppIndicator: Boolean = false,
    val showDisabledApps: Boolean = true,
    val showDisabledAppIndicator: Boolean = false,
    val sortBy: String = ApplicationModel.NAME,
    val sortOrder: String = GetApplicationsQuery.ASC,
)
