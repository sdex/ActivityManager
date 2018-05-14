package com.sdex.activityrunner.app

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg
import java.util.*

class ApplicationsListViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private var liveData: MutableLiveData<List<ApplicationModel>> = MutableLiveData()

  fun getItems(searchText: String?): LiveData<List<ApplicationModel>> {
    async(UI) {
      val data: Deferred<MutableList<ApplicationModel>> = bg {
        getApplicationsList(searchText)
      }
      liveData.value = data.await()
    }
    return liveData
  }

  private fun getApplicationsList(searchText: String?): MutableList<ApplicationModel> {
    var list: MutableList<ApplicationModel> = ArrayList()

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
      list = list.filter { it.name.contains(searchText, true) }.toMutableList()
    }

    list.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name }))
    return list
  }

  private fun addInfo(pm: PackageManager,
                      applications: MutableList<ApplicationModel>,
                      packageName: String) {
    try {
      val info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
      val model = getApplicationModel(pm, packageName, info)
      if (info.activities != null) {
        model.activitiesCount = info.activities.size
        model.exportedActivitiesCount = getExportedActivitiesCount(info.activities)
        applications.add(model)
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
