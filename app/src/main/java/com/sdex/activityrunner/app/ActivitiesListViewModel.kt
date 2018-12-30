package com.sdex.activityrunner.app

import android.app.Application
import android.content.ComponentName
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sdex.activityrunner.preferences.AppPreferences
import kotlinx.coroutines.*
import java.util.*

class ActivitiesListViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private val appPreferences: AppPreferences by lazy { AppPreferences(application) }
  private val liveData: MutableLiveData<List<ActivityModel>> = MutableLiveData()
  private var job: Job? = null

  fun getItems(packageName: String, searchText: String?): LiveData<List<ActivityModel>> {
    job?.cancel()
    Log.d("ActivitiesListViewModel", "get items: searchText=$searchText")
    job = GlobalScope.launch {
      val list = ArrayList<ActivityModel>()
      val showNotExported = appPreferences.showNotExported
      try {
        val info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        if (info.activities != null) {
          if (!searchText.isNullOrEmpty()) {
            for (activityInfo in info.activities) {
              if (!isActive) {
                break
              }
              val activityModel = getActivityModel(packageManager, activityInfo)
              if (activityModel.className.contains(searchText, true) ||
                activityModel.name.contains(searchText, true)) {
                if (activityModel.exported) {
                  list.add(activityModel)
                } else if (showNotExported) {
                  list.add(activityModel)
                }
              }
            }
          } else {
            for (activityInfo in info.activities) {
              if (!isActive) {
                break
              }
              val activityModel = getActivityModel(packageManager, activityInfo)
              if (activityModel.exported) {
                list.add(activityModel)
              } else if (showNotExported) {
                list.add(activityModel)
              }
            }
          }
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
      list.sortBy { it.name }
      Log.d("ActivitiesListViewModel", "items loaded")

      withContext(Dispatchers.Main) {
        Log.d("ActivitiesListViewModel", "set items")
        liveData.value = list
      }
    }
    return liveData
  }

  fun reloadItems(packageName: String) {
    GlobalScope.launch {
      val activitiesList = getActivitiesList(packageName)
      liveData.postValue(activitiesList)
    }
  }

  private fun getActivityModel(pm: PackageManager, activityInfo: ActivityInfo): ActivityModel {
    var activityName = try {
      activityInfo.loadLabel(pm).toString()
    } catch (e: Exception) {
      val componentName = ComponentName(activityInfo.packageName, activityInfo.name)
      componentName.shortClassName
    }
    if (activityName.isNullOrBlank()) {
      val applicationInfo = activityInfo.applicationInfo
      activityName = if (applicationInfo != null) {
        pm.getApplicationLabel(applicationInfo).toString()
      } else {
        activityInfo.packageName
      }
    }
    return ActivityModel(activityName, activityInfo.packageName, activityInfo.name,
      activityInfo.exported && activityInfo.isEnabled)
  }
}
