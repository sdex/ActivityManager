package com.sdex.activityrunner.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.sdex.activityrunner.app.launcher.ShizukuRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultShizukuSetupChecker(
    private val context: Context,
) : ShizukuSetupChecker {

    override fun isShizukuInstalled(): Boolean = try {
        context.packageManager.getPackageInfo(SHIZUKU_PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    override fun isShizukuRunning(): Boolean = ShizukuRunner.isServiceRunning()

    override fun isShizukuPermissionGranted(): Boolean = ShizukuRunner.isPermissionGranted()

    override fun isWriteSecureSettingsGranted(): Boolean =
        context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    override fun requestShizukuPermission(onResult: (granted: Boolean) -> Unit) =
        ShizukuRunner.requestPermission(onResult)

    override suspend fun grantWriteSecureSettings(): Boolean = withContext(Dispatchers.IO) {
        ShizukuRunner.grantWriteSecureSettings(context.packageName)
    }

    private companion object {
        const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    }
}
