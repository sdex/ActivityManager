package com.sdex.activityrunner.preferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    cacheRepository: CacheRepository,
) : ViewModel() {

    val state: StateFlow<PreferencesState> = appPreferences.preferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PreferencesState.fromAppPreferences(appPreferences),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: Flow<List<ApplicationModel>> = appPreferences.displayConfig
        .flatMapLatest { displayConfig ->
            cacheRepository.getApplications(GetApplicationsQuery(displayConfig).sqLiteQuery)
        }

    fun handleIntent(intent: PreferencesIntent) {
        when (intent) {
            is PreferencesIntent.SortByName -> {
                appPreferences.sortBy = ApplicationModel.NAME
            }

            is PreferencesIntent.SortByUpdateTime -> {
                appPreferences.sortBy = ApplicationModel.UPDATE_TIME
            }

            is PreferencesIntent.SortByInstallTime -> {
                appPreferences.sortBy = ApplicationModel.INSTALL_TIME
            }

            is PreferencesIntent.SortOrderAsc -> {
                appPreferences.sortOrder = GetApplicationsQuery.ASC
            }

            is PreferencesIntent.SortOrderDesc -> {
                appPreferences.sortOrder = GetApplicationsQuery.DESC
            }

            is PreferencesIntent.ToggleSystemApps -> {
                appPreferences.isShowSystemApps = intent.value
            }

            is PreferencesIntent.ToggleSystemAppIndicator -> {
                appPreferences.isShowSystemAppIndicator = intent.value
            }

            is PreferencesIntent.ToggleDisabledApps -> {
                appPreferences.isShowDisabledApps = intent.value
            }

            is PreferencesIntent.ToggleDisabledAppIndicator -> {
                appPreferences.isShowDisabledAppIndicator = intent.value
            }

            is PreferencesIntent.ToggleNonExportedActivities -> {
                appPreferences.showNotExported = intent.value
            }

            is PreferencesIntent.ToggleTheme -> {
                appPreferences.theme = intent.value

                AppCompatDelegate.setDefaultNightMode(intent.value)
            }
        }
    }
}
