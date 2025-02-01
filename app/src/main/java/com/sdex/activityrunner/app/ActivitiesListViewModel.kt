package com.sdex.activityrunner.app

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UiData(
    val application: ApplicationModel?,
    val activities: List<ActivityModel>,
)

@HiltViewModel
class ActivitiesListViewModel @Inject constructor(
    private val packageInfoProvider: PackageInfoProvider,
    private val appPreferences: AppPreferences,
    private val cacheRepository: CacheRepository,
) : ViewModel() {

    private val liveData = MutableLiveData<UiData>()
    val uiState: Flow<UiData> = liveData.asFlow()

    private lateinit var list: List<ActivityModel>

    fun getItems(packageName: String, application: ApplicationModel?): LiveData<UiData> {
        viewModelScope.launch(Dispatchers.IO) {
            val app = application ?: (cacheRepository.getApplication(packageName)
                ?: packageInfoProvider.getApplication(packageName))
            list = getActivitiesList(packageName, appPreferences.showNotExported)
            liveData.postValue(UiData(app, list))
        }
        return liveData
    }

    fun reloadItems(packageName: String, showNotExported: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            list = getActivitiesList(packageName, showNotExported)
            liveData.postValue(
                liveData.value?.copy(
                    activities = list
                )
            )
        }
    }

    fun filterItems(searchText: String?) {
        if (::list.isInitialized) {
            if (searchText != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val filteredList = list.filter {
                        it.name.contains(searchText, true) ||
                            it.className.contains(searchText, true) ||
                            (!it.label.isNullOrEmpty() && it.label.contains(searchText, true))
                    }
                    liveData.postValue(
                        liveData.value?.copy(
                            activities = filteredList
                        )
                    )
                }
            } else {
                liveData.value = liveData.value?.copy(
                    activities = list
                )
            }
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
