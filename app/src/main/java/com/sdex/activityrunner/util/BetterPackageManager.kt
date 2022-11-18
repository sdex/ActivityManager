package com.sdex.activityrunner.util

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import java.io.File

@Suppress("DEPRECATION")
fun getComponentIcon(pm: PackageManager, componentName: ComponentName): Drawable {
    return try {
        val intent = Intent()
        intent.component = componentName
        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            pm.resolveActivity(intent, 0)
        }
        resolveInfo?.loadIcon(pm) ?: pm.defaultActivityIcon
    } catch (e: Exception) {
        pm.defaultActivityIcon
    }
}

@Suppress("DEPRECATION")
fun getInstalledPackages(pm: PackageManager): List<String> {
    val packages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
    } else {
        pm.getInstalledPackages(0)
    }
    val intent = Intent(Intent.ACTION_MAIN)
    return packages
        .map { it.packageName }
        .ifEmpty {
            val intentActivities = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                pm.queryIntentActivities(intent, 0)
            }
            intentActivities.map { it.activityInfo.applicationInfo.packageName }
        }
}

@Suppress("DEPRECATION")
fun getPackageInfo(pm: PackageManager, packageName: String): PackageInfo {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(
                    PackageManager.GET_ACTIVITIES.toLong()
                )
            )
        } else {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        }
    } catch (e: Exception) {
        getApkPackageInfo(pm, packageName)
    }
}

@Suppress("DEPRECATION")
private fun getApkPackageInfo(pm: PackageManager, packageName: String): PackageInfo {
    try {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(
                    PackageManager.GET_META_DATA.toLong()
                )
            )
        } else {
            pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
        }
        val file = File(info.applicationInfo.publicSourceDir)
        val archiveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageArchiveInfo(
                file.absolutePath,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            )
        } else {
            pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_ACTIVITIES)
        }
        info.activities = archiveInfo?.activities
        return info
    } catch (e: Exception) {
        throw e
    }
}
