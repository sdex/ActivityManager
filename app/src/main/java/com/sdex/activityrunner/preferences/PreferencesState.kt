package com.sdex.activityrunner.preferences

data class PreferencesState(
    val refresh: Boolean = false,
    val sortBy: String,
    val sortOrder: String,
    val isShowSystemApps: Boolean,
    val isShowSystemAppIndicator: Boolean,
    val isShowDisabledApps: Boolean,
    val isShowDisabledAppIndicator: Boolean,
    val isShowNonExportedActivities: Boolean,
    val theme: Int,
)
