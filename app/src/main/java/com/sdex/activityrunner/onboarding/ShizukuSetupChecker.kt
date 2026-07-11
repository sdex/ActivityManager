package com.sdex.activityrunner.onboarding

/**
 * Abstracts the Shizuku/permission checks the onboarding needs, so [ShizukuOnboardingViewModel]
 * stays free of Android framework and Shizuku statics and can be unit tested with a fake.
 */
interface ShizukuSetupChecker {

    fun isShizukuInstalled(): Boolean
    fun isShizukuRunning(): Boolean
    fun isShizukuPermissionGranted(): Boolean
    fun isWriteSecureSettingsGranted(): Boolean

    /** Shows the Shizuku authorization prompt; [onResult] is invoked on the main thread. */
    fun requestShizukuPermission(onResult: (granted: Boolean) -> Unit)

    /** Grants WRITE_SECURE_SETTINGS to this app via Shizuku (`pm grant`). Returns success. */
    suspend fun grantWriteSecureSettings(): Boolean
}
