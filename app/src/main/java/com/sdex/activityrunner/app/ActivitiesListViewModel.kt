package com.sdex.activityrunner.app

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.commons.pm.getActivities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivitiesListViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager
    private val appPreferences by lazy { AppPreferences(application) }

    private val liveData = MutableLiveData<List<ActivityModel>>()
    private var list: List<ActivityModel>? = null

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

    fun filterItems(packageName: String, searchText: String?) {
        if (list != null) {
            if (searchText != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val filteredList = list!!.filter {
                        it.name.contains(searchText, true) || it.className.contains(
                            searchText,
                            true
                        )
                    }
                    liveData.postValue(filteredList)
                }
            } else {
                liveData.value = list
            }
        } else {
            getItems(packageName)
        }
    }

    @WorkerThread
    private fun getActivitiesList(packageName: String): List<ActivityModel> {
        val list = ArrayList<ActivityModel>()
        val showNotExported = appPreferences.showNotExported
        try {
            val info = getActivities(packageManager, packageName)
            for (activityInfo in info.activities) {
                val activityModel = getActivityModel(activityInfo)
                if (activityModel.exported) {
                    list.add(activityModel)
                } else if (showNotExported) {
                    list.add(activityModel)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list.sortedBy { it.name }
    }

    private fun getActivityModel(activityInfo: ActivityInfo): ActivityModel {
        return ActivityModel(
            activityInfo.name.split(".").last(),
            activityInfo.packageName, activityInfo.name,
            activityInfo.exported && activityInfo.isEnabled
        )
    }
}
