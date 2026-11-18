package com.sdex.activityrunner.preferences

data class PreferencesState(
    val sortBy: String,
    val sortOrder: String,
    val isShowSystemApps: Boolean,
    val isShowSystemAppIndicator: Boolean,
    val isShowDisabledApps: Boolean,
    val isShowDisabledAppIndicator: Boolean,
    val isHighlightDisabledActivities: Boolean = true,
    val isShowNonExportedActivities: Boolean,
    val theme: Int,
) {
    companion object {
        fun fromAppPreferences(appPreferences: AppPreferences): PreferencesState {
            return PreferencesState(
                sortBy = appPreferences.sortBy,
                sortOrder = appPreferences.sortOrder,
                isShowSystemApps = appPreferences.isShowSystemApps,
                isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator,
                isShowDisabledApps = appPreferences.isShowDisabledApps,
                isShowDisabledAppIndicator = appPreferences.isShowDisabledAppIndicator,
                isHighlightDisabledActivities = appPreferences.isHighlightDisabledActivities,
                isShowNonExportedActivities = appPreferences.showNotExported,
                theme = appPreferences.theme,
            )
        }
    }
}
