package com.sdex.activityrunner.app

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import com.sdex.activityrunner.preferences.SortingPreferences
import java.util.*

class ApplicationListViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private val sortingPreferences: SortingPreferences
  private var liveData: MutableLiveData<List<ApplicationModel>>? = null

  init {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    sortingPreferences = SortingPreferences(sharedPreferences)
  }

  fun getItems(searchText: String?): LiveData<List<ApplicationModel>> {
    if (liveData == null) {
      liveData = MutableLiveData()
    }

    val list = ArrayList<ApplicationModel>()

    val installedPackages = packageManager.getInstalledPackages(0)
    for (installedPackage in installedPackages) {
      val packageName = installedPackage.packageName
      addInfo(packageManager, list, packageName)
    }

    if (installedPackages.isEmpty()) {
      val packages = HashSet<String>()
      val intentToResolve = Intent(Intent.ACTION_MAIN)
      val ril = packageManager.queryIntentActivities(intentToResolve, 0)
      for (resolveInfo in ril) {
        packages.add(resolveInfo.activityInfo.applicationInfo.packageName)
      }
      for (packageName in packages) {
        addInfo(packageManager, list, packageName)
      }
    }

    if (searchText != null) {
      list.filter { it.name.contains(searchText, true) }
    }

    list.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))

    liveData!!.value = list
    return liveData!!
  }

  private fun addInfo(pm: PackageManager,
                      applications: MutableList<ApplicationModel>,
                      packageName: String) {
    try {
      val info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      val model = getApplicationModel(pm, packageName, info)
      applications.add(model)
      if (info.activities != null) {
        model.activitiesCount = info.activities.size
        model.exportedActivitiesCount = getExportedActivitiesCount(info.activities)
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun getApplicationModel(pm: PackageManager, packageName: String,
                                  info: PackageInfo): ApplicationModel {
    val applicationInfo = info.applicationInfo
    val name = if (applicationInfo != null) {
      pm.getApplicationLabel(applicationInfo).toString()
    } else {
      info.packageName
    }
    return ApplicationModel(name, packageName)
  }

  private fun getExportedActivitiesCount(activities: Array<ActivityInfo>): Int {
    var count = 0
    for (activity in activities) {
      if (activity.isEnabled && activity.exported) {
        count++
      }
    }
    return count
  }
}
