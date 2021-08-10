package com.sdex.activityrunner.service

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.commons.pm.getActivities

class ApplicationListLoader {

    fun syncDatabase(context: Context) {
        val applicationsModelDao = CacheDatabase.getDatabase(context).applicationsModelDao

        val oldList = applicationsModelDao.getApplicationModels()
        val newList = getApplicationsList(context)

        val listToDelete = getListToDelete(oldList, newList)
        val listToInsert = getListToInsert(oldList, newList)
        val listToUpdate = getListToUpdate(oldList, newList)

        Log.d(TAG, "listToDelete ${listToDelete.size}")
        Log.d(TAG, "listToInsert ${listToInsert.size}")
        Log.d(TAG, "listToUpdate ${listToUpdate.size}")

        if (listToDelete.isNotEmpty()) {
            val count = applicationsModelDao.delete(listToDelete)
            Log.d(TAG, "Deleted $count records")
        }

        if (listToInsert.isNotEmpty()) {
            val ids = applicationsModelDao.insert(listToInsert)
            Log.d(TAG, "Inserted ${ids.size} records")
        }

        if (listToUpdate.isNotEmpty()) {
            listToUpdate.sortBy { a -> a.name }
            val count = applicationsModelDao.update(listToUpdate)
            Log.d(TAG, "Updated $count records")
        }
    }

    private fun getListToInsert(
        oldList: MutableList<ApplicationModel>,
        newList: MutableList<ApplicationModel>
    ): MutableList<ApplicationModel> {
        val newListCopy = newList.toMutableList()
        newListCopy.removeAll(oldList)
        return newListCopy
    }

    private fun getListToDelete(
        oldList: MutableList<ApplicationModel>,
        newList: MutableList<ApplicationModel>
    ): MutableList<ApplicationModel> {
        val oldListCopy = oldList.toMutableList()
        oldListCopy.removeAll(newList)
        return oldListCopy
    }

    private fun getListToUpdate(
        oldList: MutableList<ApplicationModel>,
        newList: MutableList<ApplicationModel>
    ): MutableList<ApplicationModel> {
        return oldList.intersect(newList).toMutableList()
    }

    private fun getApplicationsList(context: Context): MutableList<ApplicationModel> {
        val list: MutableList<ApplicationModel> = ArrayList()
        val packageManager = context.packageManager
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

    private fun addInfo(
        pm: PackageManager,
        applications: MutableList<ApplicationModel>,
        packageName: String
    ) {
        try {
            val info = getActivities(pm, packageName)
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

    private fun getApplicationModel(
        pm: PackageManager, packageName: String,
        info: PackageInfo
    ): ApplicationModel {
        val applicationInfo = info.applicationInfo
        val name = if (applicationInfo != null) {
            pm.getApplicationLabel(applicationInfo).toString()
        } else {
            info.packageName
        }
        val model = ApplicationModel(packageName, name)
        applicationInfo?.let {
            model.system = (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        }
        return model
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

        const val TAG = "ApplicationListLoader"
    }
}
