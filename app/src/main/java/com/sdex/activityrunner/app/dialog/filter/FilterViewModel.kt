package com.sdex.activityrunner.app.dialog.filter

import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(
        FilterState(
            sortBy = appPreferences.sortBy,
            sortOrder = appPreferences.sortOrder,
            isShowSystemApps = appPreferences.isShowSystemApps,
            isShowSystemAppIndicator = appPreferences.isShowSystemAppIndicator,
            isShowDisabledApps = appPreferences.isShowDisabledApps,
            isShowDisabledAppIndicator = appPreferences.isShowDisabledAppIndicator,
        ),
    )
    val state = _state.asStateFlow()

    fun onSortByChanged(sortBy: String) {
        appPreferences.sortBy = sortBy
        _state.update { it.copy(sortBy = sortBy) }
    }

    fun onSortOrderChanged(sortOrder: String) {
        appPreferences.sortOrder = sortOrder
        _state.update { it.copy(sortOrder = sortOrder) }
    }

    fun onShowSystemAppsChanged(isShowSystemApps: Boolean) {
        appPreferences.isShowSystemApps = isShowSystemApps
        _state.update { it.copy(isShowSystemApps = isShowSystemApps) }
    }

    fun onShowSystemAppIndicatorChanged(isShowSystemAppIndicator: Boolean) {
        appPreferences.isShowSystemAppIndicator = isShowSystemAppIndicator
        _state.update { it.copy(isShowSystemAppIndicator = isShowSystemAppIndicator) }
    }

    fun onShowDisabledAppsChanged(isShowDisabledApps: Boolean) {
        appPreferences.isShowDisabledApps = isShowDisabledApps
        _state.update { it.copy(isShowDisabledApps = isShowDisabledApps) }
    }

    fun onShowDisabledAppIndicatorChanged(isShowDisabledAppIndicator: Boolean) {
        appPreferences.isShowDisabledAppIndicator = isShowDisabledAppIndicator
        _state.update { it.copy(isShowDisabledAppIndicator = isShowDisabledAppIndicator) }
    }
}
