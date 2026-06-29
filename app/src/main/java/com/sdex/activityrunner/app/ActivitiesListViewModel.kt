package com.sdex.activityrunner.app

import androidx.annotation.WorkerThread
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.di.IoDispatcher
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@Stable
data class UiData(
    val application: ApplicationModel? = null,
    val activities: List<ActivityModel> = emptyList(),
    val allActivities: List<ActivityModel> = emptyList(),
    val searchText: String? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ActivitiesListViewModel @Inject constructor(
    private val packageInfoProvider: PackageInfoProvider,
    private val appPreferences: AppPreferences,
    private val cacheRepository: CacheRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiData())
    val uiState: StateFlow<UiData> = _uiState.asStateFlow()

    var showNotExported
        get() = appPreferences.showNotExported
        set(value) {
            appPreferences.showNotExported = value
        }

    var isNotExportedDialogShown
        get() = appPreferences.isNotExportedDialogShown
        set(value) {
            appPreferences.isNotExportedDialogShown = value
        }

    val shouldShowNotExportedMessageDialog: Boolean
        get() = !appPreferences.showNotExported &&
            !appPreferences.isNotExportedDialogShown &&
            appPreferences.appOpenCounter > 3

    fun getItems(packageName: String, application: ApplicationModel?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val app = withContext(ioDispatcher) {
                application ?: (cacheRepository.getApplication(packageName)
                    ?: packageInfoProvider.getApplication(packageName))
            }
            val activities = withContext(ioDispatcher) {
                getActivitiesList(packageName, appPreferences.showNotExported)
            }
            _uiState.update {
                it.copy(
                    application = app,
                    activities = getFilteredList(activities, it.searchText),
                    allActivities = activities,
                    isLoading = false,
                )
            }
        }
    }

    fun reloadItems(packageName: String, showNotExported: Boolean) {
        viewModelScope.launch {
            val activities = withContext(ioDispatcher) {
                getActivitiesList(packageName, showNotExported)
            }
            _uiState.update {
                it.copy(
                    activities = getFilteredList(activities, it.searchText),
                    allActivities = activities,
                )
            }
        }
    }

    fun filterItems(searchText: String?) {
        val normalizedSearchText = normalizeSearchText(searchText)
        _uiState.update {
            if (it.searchText == normalizedSearchText) {
                it
            } else {
                it.copy(
                    activities = getFilteredList(it.allActivities, normalizedSearchText),
                    searchText = normalizedSearchText,
                )
            }
        }
    }

    private fun normalizeSearchText(searchText: String?): String? =
        searchText?.takeIf { it.isNotEmpty() }

    private fun getFilteredList(
        activities: List<ActivityModel>,
        searchText: String?,
    ): List<ActivityModel> {
        searchText ?: return activities
        return activities.filter {
            it.name.contains(searchText, true) ||
                it.className.contains(searchText, true) ||
                (!it.label.isNullOrEmpty() && it.label.contains(searchText, true))
        }
    }

    @WorkerThread
    private fun getActivitiesList(packageName: String, showNotExported: Boolean) = try {
        packageInfoProvider.getActivities(packageName)
            .filter { it.exported || showNotExported }
            .sortedBy { it.name }
    } catch (e: Exception) {
        Timber.e(e)
        emptyList()
    }
}
