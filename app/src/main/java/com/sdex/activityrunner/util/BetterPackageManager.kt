package com.sdex.activityrunner.util

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.io.File

fun getComponentIcon(pm: PackageManager, componentName: ComponentName): Drawable {
    return try {
        val intent = Intent()
        intent.component = componentName
        val resolveInfo = pm.resolveActivity(intent, 0)
        resolveInfo?.loadIcon(pm) ?: pm.defaultActivityIcon
    } catch (e: Exception) {
        pm.defaultActivityIcon
    }
}

fun getInstalledPackages(pm: PackageManager): List<String> {
    return pm.getInstalledPackages(0)
        .map { it.packageName }
        .ifEmpty {
            pm.queryIntentActivities(Intent(Intent.ACTION_MAIN), 0)
                .map { it.activityInfo.applicationInfo.packageName }
        }
}

fun getPackageInfo(pm: PackageManager, packageName: String): PackageInfo {
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    } catch (e: Exception) {
        getApkPackageInfo(pm, packageName)
    }
}

private fun getApkPackageInfo(pm: PackageManager, packageName: String): PackageInfo {
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
