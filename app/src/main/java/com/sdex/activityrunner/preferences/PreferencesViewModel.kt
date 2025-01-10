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
        PreferencesState(
            sortBy = appPreferences.sortBy,
            sortOrder = appPreferences.sortOrder,
            isShowSystemApps = appPreferences.isShowSystemApps,
            isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator,
            isShowDisabledApps = appPreferences.isShowDisabledApps,
            isShowDisabledAppIndicator = appPreferences.isShowDisabledAppIndicator,
            isShowNonExportedActivities = appPreferences.showNotExported,
            theme = appPreferences.theme,
        ),
    )
    val state = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val items: Flow<List<ApplicationModel>> = _state.flatMapLatest {
        cacheRepository.getApplications(GetApplicationsQuery(appPreferences).sqLiteQuery)
            .asFlow()
    }

    fun onSortByChanged(sortBy: String) {
        appPreferences.sortBy = sortBy
        _state.update {
            it.copy(
                refresh = true,
                sortBy = sortBy,
            )
        }
    }

    fun onSortOrderChanged(sortOrder: String) {
        appPreferences.sortOrder = sortOrder
        _state.update {
            it.copy(
                refresh = true,
                sortOrder = sortOrder,
            )
        }
    }

    fun onShowSystemAppsChanged(isShowSystemApps: Boolean) {
        appPreferences.isShowSystemApps = isShowSystemApps
        _state.update {
            it.copy(
                refresh = true,
                isShowSystemApps = isShowSystemApps,
            )
        }
    }

    fun onShowSystemAppIndicatorChanged(isShowSystemAppIndicator: Boolean) {
        appPreferences.isShowSystemAppIndicator = isShowSystemAppIndicator
        _state.update {
            it.copy(
                refresh = false,
                isShowSystemAppIndicator = isShowSystemAppIndicator,
            )
        }
    }

    fun onShowDisabledAppsChanged(isShowDisabledApps: Boolean) {
        appPreferences.isShowDisabledApps = isShowDisabledApps
        _state.update {
            it.copy(
                refresh = true,
                isShowDisabledApps = isShowDisabledApps,
            )
        }
    }

    fun onShowDisabledAppIndicatorChanged(isShowDisabledAppIndicator: Boolean) {
        appPreferences.isShowDisabledAppIndicator = isShowDisabledAppIndicator
        _state.update {
            it.copy(
                refresh = false,
                isShowDisabledAppIndicator = isShowDisabledAppIndicator,
            )
        }
    }

    fun onShowNonExportedActivitiesChanged(isShowNonExportedActivities: Boolean) {
        appPreferences.showNotExported = isShowNonExportedActivities
        _state.update {
            it.copy(
                refresh = true,
                isShowNonExportedActivities = isShowNonExportedActivities,
            )
        }
    }

    fun onThemeChanged(theme: Int) {
        appPreferences.theme = theme
        _state.update {
            it.copy(
                refresh = false,
                theme = theme,
            )
        }
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}
