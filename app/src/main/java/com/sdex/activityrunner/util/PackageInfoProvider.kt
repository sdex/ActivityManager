package com.sdex.activityrunner.util

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.ChangedPackages
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import androidx.annotation.RequiresApi
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel
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
        val applicationInfo = packageInfo.applicationInfo
        val isSystemApp = applicationInfo != null &&
            (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isEnabled = applicationInfo != null && applicationInfo.enabled
        val activities = getActivities(packageInfo)
        val activitiesCount = activities.size
        val exportedActivitiesCount = activities.count { it.exported }

        ApplicationModel(
            packageName = packageName,
            name = getApplicationName(packageInfo),
            activitiesCount = activitiesCount,
            exportedActivitiesCount = exportedActivitiesCount,
            system = isSystemApp,
            enabled = isEnabled,
            versionCode = packageInfo.getVersionCodeCompat(),
            versionName = packageInfo.versionName ?: "",
            updateTime = packageInfo.lastUpdateTime,
            installTime = packageInfo.firstInstallTime,
            installerPackage = getInstallerPackage(packageName),
        )
    } catch (e: Exception) {
        Timber.e(e, "Failed to process: $packageName")
        null
    }

    fun getActivities(packageName: String): List<ActivityModel> {
        val packageInfo = getPackageInfo(packageName)
        val applicationInfo = packageInfo.applicationInfo
            ?: return emptyList()
        val activityInfos = if (applicationInfo.enabled) {
            packageInfo.activities
        } else {
            getPackageArchiveInfo(packageManager, packageName).activities
        }
        return activityInfos?.map { it.toActivityModel() } ?: emptyList()
    }

    fun getActivities(packageInfo: PackageInfo): List<ActivityModel> {
        val applicationInfo = packageInfo.applicationInfo
            ?: return emptyList()
        val activityInfos = if (applicationInfo.enabled) {
            packageInfo.activities
        } else {
            getPackageArchiveInfo(packageManager, packageInfo.packageName).activities
        }
        return activityInfos?.map { it.toActivityModel() } ?: emptyList()
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
            .ifEmpty { // fallback if getInstalledPackages fails
                val intentActivities = if (isAndroidT()) {
                    packageManager.queryIntentActivities(
                        intent,
                        PackageManager.ResolveInfoFlags.of(0),
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
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()),
                )
            } else {
                packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            }
        } catch (_: Exception) {
            getPackageArchiveInfo(packageManager, packageName)
        }
    }

    fun getPublicSourceDir(packageName: String): String? {
        val packageInfo = getPackageInfo(packageName)
        return packageInfo.applicationInfo?.publicSourceDir
    }

    private fun getApplicationName(packageInfo: PackageInfo): String {
        val applicationInfo = packageInfo.applicationInfo
        return if (applicationInfo != null) {
            packageManager.getApplicationLabel(applicationInfo).toString()
        } else {
            packageInfo.packageName
        }
    }

    fun getResourcesForApplication(packageName: String): Resources {
        return packageManager.getResourcesForApplication(packageName)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getChangedPackages(lastSequenceNumber: Int): ChangedPackages? =
        packageManager.getChangedPackages(lastSequenceNumber)

    private fun getPackageArchiveInfo(pm: PackageManager, packageName: String): PackageInfo {
        try {
            val info = if (isAndroidT()) {
                pm.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_META_DATA.toLong()),
                )
            } else {
                pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            }
            val applicationInfo = info.applicationInfo
                ?: throw IllegalStateException("ApplicationInfo is null")
            val archiveInfo = getPackageArchiveActivities(pm, applicationInfo)
            info.activities = archiveInfo?.activities
            return info
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getPackageArchiveActivities(
        pm: PackageManager,
        applicationInfo: ApplicationInfo,
    ): PackageInfo? {
        val file = File(applicationInfo.publicSourceDir)
        return if (isAndroidT()) {
            pm.getPackageArchiveInfo(
                file.absolutePath,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong()),
            )
        } else {
            pm.getPackageArchiveInfo(file.absolutePath, PackageManager.GET_ACTIVITIES)
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

    private fun getInstallerPackage(packageName: String): String? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            packageManager.getInstallSourceInfo(packageName).installingPackageName
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstallerPackageName(packageName)
        }
    } catch (_: Exception) {
        null
    }

    companion object {

        const val GOOGLE_PLAY_INSTALLER = "com.android.vending"
        const val ANDROID_INSTALLER = "com.google.android.packageinstaller"
        const val FDROID_INSTALLER = "org.fdroid.fdroid"

        fun PackageInfo.getVersionCodeCompat(): Long =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                longVersionCode
            } else {
                @Suppress("DEPRECATION")
                versionCode.toLong()
            }
    }
}
