package com.sdex.activityrunner.service

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.support.v4.app.JobIntentService
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import java.util.*

class AppLoaderIntentService : JobIntentService() {

  override fun onHandleWork(intent: Intent) {
    val applicationsModelDao = CacheDatabase.getDatabase(applicationContext).applicationsModelDao

    val oldList = applicationsModelDao.getApplicationModels()
    val newList = getApplicationsList()

    val listToInsert = getListToInsert(oldList, newList)
    val listToDelete = getListToDelete(oldList, newList)

    if (listToInsert.isNotEmpty()) {
      applicationsModelDao.insert(*listToInsert.toTypedArray())
    }

    if (listToDelete.isNotEmpty()) {
      applicationsModelDao.delete(*listToDelete.toTypedArray())
    }
  }

  private fun getListToInsert(oldList: MutableList<ApplicationModel>,
                              newList: MutableList<ApplicationModel>): MutableList<ApplicationModel> {
    val newListCopy = newList.toMutableList()
    newListCopy.removeAll(oldList)
    return newListCopy
  }

  private fun getListToDelete(oldList: MutableList<ApplicationModel>,
                              newList: MutableList<ApplicationModel>): MutableList<ApplicationModel> {
    val oldListCopy = oldList.toMutableList()
    oldListCopy.removeAll(newList)
    return oldListCopy
  }

  private fun getApplicationsList(): MutableList<ApplicationModel> {
    val list: MutableList<ApplicationModel> = ArrayList()

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
      }
      applications.add(model)
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

  companion object {

    private const val JOB_ID = 1212

    fun enqueueWork(context: Context, work: Intent) {
      JobIntentService.enqueueWork(context, AppLoaderIntentService::class.java, JOB_ID, work)
    }
  }
}
