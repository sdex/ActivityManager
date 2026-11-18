package com.sdex.activityrunner.preferences

sealed class PreferencesIntent {
    data object SortByName : PreferencesIntent()
    data object SortByUpdateTime : PreferencesIntent()
    data object SortByInstallTime : PreferencesIntent()
    data object SortOrderAsc : PreferencesIntent()
    data object SortOrderDesc : PreferencesIntent()
    data class ToggleSystemApps(val value: Boolean) : PreferencesIntent()
    data class ToggleSystemAppIndicator(val value: Boolean) : PreferencesIntent()
    data class ToggleDisabledApps(val value: Boolean) : PreferencesIntent()
    data class ToggleDisabledAppIndicator(val value: Boolean) : PreferencesIntent()
    data class ToggleNonExportedActivities(val value: Boolean) : PreferencesIntent()
    data class ToggleTheme(val value: Int) : PreferencesIntent()
}
