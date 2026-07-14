package com.sdex.activityrunner.preferences

import com.sdex.activityrunner.app.launcher.AssistantBackup
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

/**
 * Shared [AppPreferences] fake for unit and instrumentation tests. When the interface gains a
 * member, add a default implementation here instead of updating every test.
 */
class FakeAppPreferences(
    displayConfig: DisplayConfig = DisplayConfig(),
    override var showNotExported: Boolean = false,
    override var lastSequenceNumber: Int = -1,
    override var lastBootCount: Int = -1,
) : AppPreferences {

    val displayConfigState = MutableStateFlow(displayConfig)

    override val preferences: Flow<PreferencesState> = emptyFlow()
    override val displayConfig: Flow<DisplayConfig> = displayConfigState
    override var isNotExportedDialogShown: Boolean = false
    override var appOpenCounter: Int = 0
    override var isShowSystemApps: Boolean = true
    override var isShowSystemAppIndicator: Boolean = false
    override var isShowDisabledApps: Boolean = true
    override var isShowDisabledAppIndicator: Boolean = false
    override var showLineNumbers: Boolean = true
    override var isShowLaunchToast: Boolean = true
    override var theme: Int = 0
    override var sortBy: String = ApplicationModel.NAME
    override var sortOrder: String = GetApplicationsQuery.ASC
    override var suExecutable: String = "su"
    override var assistantBackup: AssistantBackup? = null

    override fun onAppOpened() {
        appOpenCounter++
    }
}
