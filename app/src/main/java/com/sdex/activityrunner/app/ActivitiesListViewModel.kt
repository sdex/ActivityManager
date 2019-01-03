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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ActivitiesListViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private val appPreferences: AppPreferences by lazy { AppPreferences(application) }
  private val liveData: MutableLiveData<List<ActivityModel>> = MutableLiveData()
  private var list: List<ActivityModel>? = null

  fun getItems(packageName: String): LiveData<List<ActivityModel>> {
    GlobalScope.launch {
      list = getActivitiesList(packageName)
      withContext(Dispatchers.Main) {
        liveData.value = list
      }
    }
    return liveData
  }

  fun reloadItems(packageName: String) {
    GlobalScope.launch {
      list = getActivitiesList(packageName)
      liveData.postValue(list)
    }
  }

  fun filterItems(packageName: String, searchText: String?) {
    if (list != null) {
      if (searchText != null) {
        GlobalScope.launch {
          val filteredList = list!!.filter {
            it.name.contains(searchText, true) || it.className.contains(searchText, true)
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
  private fun getActivitiesList(packageName: String): ArrayList<ActivityModel> {
    val list = ArrayList<ActivityModel>()
    val showNotExported = appPreferences.showNotExported
    try {
      val info = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      if (info.activities != null) {
        for (activityInfo in info.activities) {
          val activityModel = getActivityModel(packageManager, activityInfo)
          if (activityModel.exported) {
            list.add(activityModel)
          } else if (showNotExported) {
            list.add(activityModel)
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    list.sortBy { it.name }
    return list
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
