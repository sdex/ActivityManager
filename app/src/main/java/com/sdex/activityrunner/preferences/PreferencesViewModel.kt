package com.sdex.activityrunner.preferences

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    cacheRepository: CacheRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(
        PreferencesState.fromAppPreferences(appPreferences),
    )
    val state = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: Flow<List<ApplicationModel>> = _state.flatMapLatest {
        cacheRepository.getApplications(GetApplicationsQuery(appPreferences).sqLiteQuery)
            .asFlow()
    }

    fun handleIntent(intent: PreferencesIntent) {
        when (intent) {
            is PreferencesIntent.SortByName -> {
                appPreferences.sortBy = ApplicationModel.NAME
                _state.update { it.copy(refresh = true, sortBy = ApplicationModel.NAME) }
            }

            is PreferencesIntent.SortByUpdateTime -> {
                appPreferences.sortBy = ApplicationModel.UPDATE_TIME
                _state.update { it.copy(refresh = true, sortBy = ApplicationModel.UPDATE_TIME) }
            }

            is PreferencesIntent.SortByInstallTime -> {
                appPreferences.sortBy = ApplicationModel.INSTALL_TIME
                _state.update { it.copy(refresh = true, sortBy = ApplicationModel.INSTALL_TIME) }
            }

            is PreferencesIntent.SortOrderAsc -> {
                appPreferences.sortOrder = GetApplicationsQuery.ASC
                _state.update { it.copy(refresh = true, sortOrder = GetApplicationsQuery.ASC) }
            }

            is PreferencesIntent.SortOrderDesc -> {
                appPreferences.sortOrder = GetApplicationsQuery.DESC
                _state.update { it.copy(refresh = true, sortOrder = GetApplicationsQuery.DESC) }
            }

            is PreferencesIntent.ToggleSystemApps -> {
                appPreferences.isShowSystemApps = intent.value
                _state.update { it.copy(refresh = true, isShowSystemApps = intent.value) }
            }

            is PreferencesIntent.ToggleSystemAppIndicator -> {
                appPreferences.isShowSystemAppIndicator = intent.value
                _state.update { it.copy(refresh = false, isShowSystemAppIndicator = intent.value) }
            }

            is PreferencesIntent.ToggleDisabledApps -> {
                appPreferences.isShowDisabledApps = intent.value
                _state.update { it.copy(refresh = true, isShowDisabledApps = intent.value) }
            }

            is PreferencesIntent.ToggleDisabledAppIndicator -> {
                appPreferences.isShowDisabledAppIndicator = intent.value
                _state.update {
                    it.copy(
                        refresh = false,
                        isShowDisabledAppIndicator = intent.value,
                    )
                }
            }

            is PreferencesIntent.ToggleNonExportedActivities -> {
                appPreferences.showNotExported = intent.value
                _state.update {
                    it.copy(
                        refresh = true,
                        isShowNonExportedActivities = intent.value,
                    )
                }
            }

            is PreferencesIntent.ToggleTheme -> {
                appPreferences.theme = intent.value
                _state.update { it.copy(refresh = false, theme = intent.value) }
                AppCompatDelegate.setDefaultNightMode(intent.value)
            }
        }
    }
}
