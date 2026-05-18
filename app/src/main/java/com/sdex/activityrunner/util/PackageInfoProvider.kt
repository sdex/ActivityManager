package com.sdex.activityrunner.util

import android.content.pm.PackageInfo
import android.content.res.Resources
import android.os.Build
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel

interface PackageInfoProvider {
    fun getInstalledPackages(): List<String>
    fun getApplication(packageName: String): ApplicationModel?
    fun getActivities(packageName: String): List<ActivityModel>
    fun getPackageInfo(packageName: String): PackageInfo
    fun getResourcesForApplication(packageName: String): Resources
    fun getChangedPackages(lastSequenceNumber: Int): ChangedPackages?

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
