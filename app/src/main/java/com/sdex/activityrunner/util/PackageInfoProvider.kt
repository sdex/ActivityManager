package com.sdex.activityrunner.util

import android.content.Context
import android.content.pm.ActivityInfo
import com.sdex.activityrunner.app.ActivityModel

class PackageInfoProvider(
    context: Context,
) {

    private val packageManager = context.packageManager

    fun getActivities(packageName: String): List<ActivityModel> {
        return getPackageInfo(packageManager, packageName).activities
            .map { it.toActivityModel() }
    }

    fun isAppEnabled(packageName: String): Boolean {
        val packageInfo = getPackageInfo(packageManager, packageName)
        return packageInfo.applicationInfo.enabled
    }

    private fun ActivityInfo.toActivityModel() = ActivityModel(
        name.split(".").last(),
        packageName,
        name,
        loadLabel(packageManager).toString(),
        exported && isEnabled
    )
}
