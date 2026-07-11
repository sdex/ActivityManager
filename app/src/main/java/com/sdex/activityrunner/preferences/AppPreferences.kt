package com.sdex.activityrunner.preferences

import androidx.appcompat.app.AppCompatDelegate
import com.sdex.activityrunner.app.launcher.AssistantBackup
import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val preferences: Flow<PreferencesState>
    val displayConfig: Flow<DisplayConfig>
    var isNotExportedDialogShown: Boolean
    val appOpenCounter: Int
    var isShowSystemApps: Boolean
    var isShowSystemAppIndicator: Boolean
    var isShowDisabledApps: Boolean
    var isShowDisabledAppIndicator: Boolean
    var showNotExported: Boolean
    var showLineNumbers: Boolean
    @get:AppCompatDelegate.NightMode
    @setparam:AppCompatDelegate.NightMode
    var theme: Int
    var sortBy: String
    var sortOrder: String
    var suExecutable: String
    var lastSequenceNumber: Int
    var lastBootCount: Int
    /** Persisted secure-assistant snapshot for the restore flow; set to null to clear it. */
    var assistantBackup: AssistantBackup?
    fun onAppOpened()
}
