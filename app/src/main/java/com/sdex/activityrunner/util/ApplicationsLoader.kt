package com.sdex.activityrunner.util

import android.content.pm.ApplicationInfo
import android.os.Build
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import timber.log.Timber
import javax.inject.Inject

class ApplicationsLoader @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val packageInfoProvider: PackageInfoProvider,
) {

    fun syncDatabase() {
        val oldList = cacheRepository.getApplications()
        val newList = getApplicationsList()

        val listToDelete = oldList.toMutableList().also { it.removeAll(newList) }
        val listToInsert = newList.toMutableList().also { it.removeAll(oldList) }
        val listToUpdate = oldList.intersect(newList.toSet()).toList()

        if (listToDelete.isNotEmpty()) {
            val count = cacheRepository.delete(listToDelete)
            Timber.d("Deleted $count records")
        }

        if (listToInsert.isNotEmpty()) {
            val ids = cacheRepository.insert(listToInsert)
            Timber.d("Inserted ${ids.size} records")
        }

        if (listToUpdate.isNotEmpty()) {
            val count = cacheRepository.update(listToUpdate)
            Timber.d("Updated $count records")
        }
    }

    private fun getApplicationsList(): List<ApplicationModel> {
        return packageInfoProvider.getInstalledPackages()
            .mapNotNull { getApplication(it) }
    }

    private fun getApplication(
        packageName: String
    ): ApplicationModel? = try {
        val packageInfo = packageInfoProvider.getPackageInfo(packageName)
        val name = packageInfoProvider.getApplicationName(packageInfo)
        val activities = packageInfo.activities ?: emptyArray()
        val applicationInfo = packageInfo.applicationInfo
        val isSystemApp = if (applicationInfo != null) {
            (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } else {
            false
        }
        val versionName = packageInfo.versionName ?: ""
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            packageInfo.versionCode.toLong()
        }
        val exportedActivitiesCount = activities.count { it.isEnabled && it.exported }
        val lastUpdateTime = packageInfo.lastUpdateTime
        val installTime = packageInfo.firstInstallTime
        ApplicationModel(
            packageName = packageName,
            name = name,
            activitiesCount = activities.size,
            exportedActivitiesCount = exportedActivitiesCount,
            system = isSystemApp,
            enabled = applicationInfo.enabled,
            versionCode = versionCode,
            versionName = versionName,
            updateTime = lastUpdateTime,
            installTime = installTime,
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to process: $packageName")
        null
    }
}
