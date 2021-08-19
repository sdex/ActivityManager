package com.sdex.activityrunner.service

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.commons.pm.getPackageInfo
import timber.log.Timber

class ApplicationListLoader {

    fun syncDatabase(context: Context) {
        val applicationsModelDao = CacheDatabase.getDatabase(context).applicationsModelDao

        val oldList = applicationsModelDao.getApplicationModels()
        val newList = getApplicationsList(context)

        val listToDelete = oldList.toMutableList().also { it.removeAll(newList) }
        val listToInsert = newList.toMutableList().also { it.removeAll(oldList) }
        val listToUpdate = oldList.intersect(newList).toList()

        Timber.d("listToDelete " + listToDelete.size)
        Timber.d("listToInsert " + listToInsert.size)
        Timber.d("listToUpdate " + listToUpdate.size)

        if (listToDelete.isNotEmpty()) {
            val count = applicationsModelDao.delete(listToDelete)
            Timber.d("Deleted $count records")
        }

        if (listToInsert.isNotEmpty()) {
            val ids = applicationsModelDao.insert(listToInsert)
            Timber.d("Inserted ${ids.size} records")
        }

        if (listToUpdate.isNotEmpty()) {
            val count = applicationsModelDao.update(listToUpdate)
            Timber.d("Updated $count records")
        }
    }

    private fun getApplicationsList(context: Context): List<ApplicationModel> {
        val packageManager = context.packageManager
        return packageManager.getInstalledPackages(0)
            .map { it.packageName }
            .ifEmpty {
                val intentToResolve = Intent(Intent.ACTION_MAIN)
                packageManager.queryIntentActivities(intentToResolve, 0)
                    .map { it.activityInfo.applicationInfo.packageName }
            }
            .mapNotNull { getApplication(packageManager, it) }
    }

    private fun getApplication(
        packageManager: PackageManager,
        packageName: String
    ): ApplicationModel? {
        try {
            val packageInfo = getPackageInfo(packageManager, packageName)
            val applicationInfo = packageInfo.applicationInfo
            val name = if (applicationInfo != null) {
                packageManager.getApplicationLabel(applicationInfo).toString()
            } else {
                packageInfo.packageName
            }
            val activities = packageInfo.activities ?: emptyArray()
            val isSystemApp = if (applicationInfo != null) {
                (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
            } else {
                false
            }
            val exportedActivitiesCount = activities.count { it.isEnabled && it.exported }
            return ApplicationModel(
                packageName,
                name,
                activities.size,
                exportedActivitiesCount,
                isSystemApp
            )
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    companion object {

        const val TAG = "ApplicationListLoader"
    }
}
