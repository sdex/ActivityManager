package com.sdex.activityrunner.app

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.getPackageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class ActivitiesListViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager
    private val appPreferences by lazy { AppPreferences(application) }

    private val liveData = MutableLiveData<List<ActivityModel>>()
    private lateinit var list: List<ActivityModel>

    fun getItems(packageName: String): LiveData<List<ActivityModel>> {
        viewModelScope.launch(Dispatchers.IO) {
            list = getActivitiesList(packageName)
            liveData.postValue(list)
        }
        return liveData
    }

    fun reloadItems(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            list = getActivitiesList(packageName)
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
    private fun getActivitiesList(packageName: String) = try {
        val showNotExported = appPreferences.showNotExported
        getPackageInfo(packageManager, packageName).activities
            .map { it.toActivityModel() }
            .filter { it.exported || showNotExported }
            .sortedBy { it.name }
    } catch (e: Exception) {
        Timber.e(e)
        emptyList()
    }

    private fun ActivityInfo.toActivityModel() = ActivityModel(
        name.split(".").last(),
        packageName,
        name,
        loadLabel(packageManager).toString(),
        exported && isEnabled
    )
}
