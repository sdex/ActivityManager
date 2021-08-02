package com.sdex.commons.pm

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import java.io.File

fun getActivities(pm: PackageManager, packageName: String): PackageInfo {
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    } catch (e: Exception) {
        getActivitiesFromApk(pm, packageName)
    }
}

@Throws(Exception::class)
private fun getActivitiesFromApk(pm: PackageManager, packageName: String): PackageInfo {
    try {
        val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
        val file = File(info.applicationInfo.publicSourceDir)
        val archiveInfo = pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_ACTIVITIES)
        info.activities = archiveInfo?.activities
        return info
    } catch (e: Exception) {
        throw e
    }
}
