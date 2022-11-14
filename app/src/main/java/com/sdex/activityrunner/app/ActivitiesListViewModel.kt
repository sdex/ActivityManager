package com.sdex.activityrunner.app

import androidx.annotation.WorkerThread
import androidx.lifecycle.*
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ActivitiesListViewModel @Inject constructor(
    private val packageInfoProvider: PackageInfoProvider,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val liveData = MutableLiveData<List<ActivityModel>>()
    private lateinit var list: List<ActivityModel>

    fun getItems(packageName: String): LiveData<List<ActivityModel>> {
        viewModelScope.launch(Dispatchers.IO) {
            list = getActivitiesList(packageName, appPreferences.showNotExported)
            liveData.postValue(list)
        }
        return liveData
    }

    fun reloadItems(packageName: String, showNotExported: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            list = getActivitiesList(packageName, showNotExported)
            liveData.postValue(list)
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
                    liveData.postValue(filteredList)
                }
            } else {
                liveData.value = list
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
