package com.sdex.activityrunner.util

import android.content.Context
import android.content.pm.ActivityInfo
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.manifest.ManifestParser
import net.dongliu.apk.parser.ApkFile

class PackageInfoProvider(
    context: Context,
) {

    private val packageManager = context.packageManager

    fun getActivities(packageName: String): List<ActivityModel> {
        val packageInfo = getPackageInfo(packageManager, packageName)
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

    private fun ActivityInfo.toActivityModel() = ActivityModel(
        name.split(".").last(),
        packageName,
        name,
        loadLabel(packageManager).toString(),
        exported,
        enabled,
    )
}
