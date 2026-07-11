package com.sdex.activityrunner.app.launcher

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.launcher.ShizukuLaunchStrategy.Companion.restorePendingBackup
import com.sdex.activityrunner.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Launches a non-exported activity by abusing the secure "assistant" setting: the target component
 * is written to [Settings.Secure] `assistant`, then a real assist event is fired so the system
 * (uid 1000) launches it on our behalf, bypassing the export check.
 *
 * Requires Shizuku (running and authorized): it injects `KEYCODE_ASSIST` (there is no app API to
 * trigger assist; firing `Intent(ACTION_ASSIST)` only shows an app chooser) and grants this app the
 * `WRITE_SECURE_SETTINGS` permission on-device (`pm grant`) needed for the swap, if not already held.
 *
 * Restoring the user's real assistant afterwards is mandatory: the original values are persisted to
 * [AppPreferences] before the swap so they survive a crash (see [restorePendingBackup], run on app
 * startup).
 */
class ShizukuLaunchStrategy(
    private val appPreferences: AppPreferences,
) : LaunchStrategy {

    override val method = LaunchMethod.SHIZUKU

    // Serializes the global "assistant" swap+restore so concurrent launches can't corrupt the backup.
    private val launchMutex = Mutex()

    override suspend fun isAvailable(context: Context): Boolean =
        ShizukuRunner.isServiceRunning() && ShizukuRunner.isPermissionGranted()

    private fun isSecureSettingsGranted(context: Context): Boolean =
        context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) ==
            PackageManager.PERMISSION_GRANTED

    override suspend fun launch(context: Context, component: ComponentName): LaunchResult {
        if (!isAvailable(context)) {
            return LaunchResult.Unavailable
        }
        // Settings/DataStore/Binder IO only — no UI work — so stay off the main thread.
        return withContext(Dispatchers.IO) {
            launchMutex.withLock {
                val resolver = context.contentResolver
                try {
                    // WRITE_SECURE_SETTINGS is needed for the swap; grant it via Shizuku if missing.
                    // If the grant does not take, the putString below throws and we report an error.
                    if (!isSecureSettingsGranted(context)) {
                        ShizukuRunner.grantWriteSecureSettings(context.packageName)
                    }

                    // Back up (persisted) before touching anything, so restore is guaranteed.
                    val originalAssistant = Settings.Secure.getString(resolver, KEY_ASSISTANT)
                    val originalVoiceInteraction =
                        Settings.Secure.getString(resolver, KEY_VOICE_INTERACTION)
                    appPreferences.assistantBackup =
                        AssistantBackup(originalAssistant, originalVoiceInteraction)

                    // Point the assistant at the target, using the legacy assist-activity path.
                    Settings.Secure.putString(resolver, KEY_ASSISTANT, component.flattenToString())
                    Settings.Secure.putString(resolver, KEY_VOICE_INTERACTION, "")

                    // Fire a real assist event so the system reads the swapped setting and launches
                    // the target. Injected via Shizuku (shell holds INJECT_EVENTS).
                    val injected = ShizukuRunner.injectAssistKey()
                    delay(ASSIST_LAUNCH_WINDOW_MS)

                    if (injected) LaunchResult.Success else LaunchResult.Error(R.string.starting_activity_intent_failed)
                } catch (e: Exception) {
                    Timber.e(e)
                    LaunchResult.Error(R.string.starting_activity_intent_failed)
                } finally {
                    // Mandatory restore.
                    restorePendingBackup(context, appPreferences)
                }
            }
        }
    }

    companion object {

        private const val KEY_ASSISTANT = "assistant"
        private const val KEY_VOICE_INTERACTION = "voice_interaction_service"
        private const val ASSIST_LAUNCH_WINDOW_MS = 2000L

        /**
         * Restores any assistant backup left behind by an interrupted launch. Call on app startup so
         * the user's real assistant is never left broken by a crash mid-swap.
         */
        fun restorePendingBackup(context: Context, appPreferences: AppPreferences) {
            val backup = appPreferences.assistantBackup ?: return
            if (context.checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            try {
                val resolver = context.contentResolver
                Settings.Secure.putString(resolver, KEY_ASSISTANT, backup.assistant)
                Settings.Secure.putString(resolver, KEY_VOICE_INTERACTION, backup.voiceInteraction)
                Timber.d("Restored assistant backup")
            } catch (e: Exception) {
                Timber.e(e, "Failed to restore assistant backup")
            } finally {
                appPreferences.assistantBackup = null
            }
        }
    }
}
