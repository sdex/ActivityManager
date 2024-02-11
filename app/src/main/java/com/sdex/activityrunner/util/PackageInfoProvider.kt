package com.sdex.activityrunner.util

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.manifest.ManifestParser
import net.dongliu.apk.parser.ApkFile
import timber.log.Timber
import java.io.File

class PackageInfoProvider(
    context: Context,
) {

    private val packageManager = context.packageManager

    fun getApplication(
        packageName: String,
    ): ApplicationModel? = try {
        val packageInfo = getPackageInfo(packageName)
        val name = getApplicationName(packageInfo)
        val activities = packageInfo.activities ?: emptyArray()
        val applicationInfo = packageInfo.applicationInfo
        val isSystemApp = if (applicationInfo != null) {
            (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } else {
            false
        }
        val versionName = packageInfo.versionName ?: ""
        val versionCode = getVersionCode(packageInfo)
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

    fun getActivities(packageName: String): List<ActivityModel> {
        val packageInfo = getPackageInfo(packageName)
        if (packageInfo.applicationInfo.enabled) {
            return packageInfo.activities.map { it.toActivityModel() }
        } else {
            val publicSourceDir = packageInfo.applicationInfo.publicSourceDir
            ApkFile(publicSourceDir).use { apkFile ->
                val manifestParser = ManifestParser(apkFile.manifestXml)
                return manifestParser.getActivities(packageName)
            }
        }
    }

    fun getInstalledPackages(): List<String> {
        val packages = if (isAndroidT()) {
            packageManager.getInstalledPackages(PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getInstalledPackages(0)
        }
        val intent = Intent(Intent.ACTION_MAIN)
        return packages
            .map { it.packageName }
            .ifEmpty {
                val intentActivities = if (isAndroidT()) {
                    packageManager.queryIntentActivities(
                        intent,
                        PackageManager.ResolveInfoFlags.of(0)
                    )
                } else {
                    packageManager.queryIntentActivities(intent, 0)
                }
                intentActivities.map { it.activityInfo.applicationInfo.packageName }
            }

    }

    fun getPackageInfo(packageName: String): PackageInfo {
        return try {
            if (isAndroidT()) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
                )
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
        } catch (e: Exception) {
            getApkPackageInfo(packageManager, packageName)
        }
    }

    private fun getApplicationName(packageInfo: PackageInfo): String {
        return if (packageInfo.applicationInfo != null) {
            packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
        } else {
            packageInfo.packageName
        }
    }

    fun getResourcesForApplication(packageName: String): Resources {
        return packageManager.getResourcesForApplication(packageName)
    }

    private fun getApkPackageInfo(pm: PackageManager, packageName: String): PackageInfo {
        try {
            val info = if (isAndroidT()) {
                pm.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong())
                )
            } else {
                pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            }
            val file = File(info.applicationInfo.publicSourceDir)
            val archiveInfo = if (isAndroidT()) {
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

    private fun isAndroidT() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private fun ActivityInfo.toActivityModel() = ActivityModel(
        name.split(".").last(),
        packageName,
        name,
        loadLabel(packageManager).toString(),
        exported,
        enabled,
    )

    companion object {

        @Suppress("DEPRECATION")
        fun getVersionCode(packageInfo: PackageInfo): Long =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                packageInfo.versionCode.toLong()
            }
    }
}
