package com.sdex.activityrunner.intent.analyzer

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import timber.log.Timber

/**
 * Controls the manifest alias that puts [IntentAnalyzerActivity] in front of the system chooser.
 *
 * The alias ships disabled (`android:enabled="false"`), so the analyzer stays out of the share and
 * open dialogs until interception is turned on in the settings. Disabling the component - rather
 * than only ignoring the intent - is what actually keeps the entry out of the chooser, because the
 * system reads the component state when it resolves an intent.
 */
object IntentInterceptor {

    /**
     * Declared in the manifest relative to the `com.sdex.activityrunner` namespace, which is not
     * the application id (the dev flavour appends a suffix to it).
     */
    private const val ALIAS = "com.sdex.activityrunner.intent.analyzer.IntentInterceptorActivity"

    fun isEnabled(context: Context): Boolean {
        val state = try {
            context.packageManager.getComponentEnabledSetting(component(context))
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "Failed to read the interceptor state")
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        }
        // DEFAULT falls back to the manifest, where the alias is declared disabled.
        return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        val state = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }
        try {
            context.packageManager.setComponentEnabledSetting(
                component(context),
                state,
                PackageManager.DONT_KILL_APP,
            )
        } catch (e: Exception) {
            Timber.w(e, "Failed to change the interceptor state")
        }
    }

    /**
     * Re-applies the stored preference when it drifted apart from the component state, which
     * happens when the preferences are restored from a backup onto a fresh install.
     */
    fun sync(context: Context, enabled: Boolean) {
        if (isEnabled(context) != enabled) {
            setEnabled(context, enabled)
        }
    }

    private fun component(context: Context) = ComponentName(context.packageName, ALIAS)
}
