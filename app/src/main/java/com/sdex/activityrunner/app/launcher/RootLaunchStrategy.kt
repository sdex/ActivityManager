package com.sdex.activityrunner.app.launcher

import android.content.ComponentName
import android.content.Context
import com.sdex.activityrunner.R
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Locale

/** Launches via `su -> am start`. Requires a rooted device. */
class RootLaunchStrategy(
    private val appPreferences: AppPreferences,
) : LaunchStrategy {

    override val method = LaunchMethod.ROOT

    override suspend fun isAvailable(context: Context): Boolean = withContext(Dispatchers.IO) {
        RootUtils.isSuAvailable(appPreferences.suExecutable)
    }

    override suspend fun launch(context: Context, component: ComponentName): LaunchResult =
        withContext(Dispatchers.IO) {
            val suExecutable = appPreferences.suExecutable
            if (!RootUtils.isSuAvailable(suExecutable)) {
                return@withContext LaunchResult.Unavailable
            }
            try {
                val packageName = component.packageName
                val className = component.className ?: return@withContext LaunchResult.Error(R.string.starting_activity_root_error)
                val output = RootUtils.launchComponent(suExecutable, packageName, className)
                Timber.d("Result: \"$output\"")
                if (isLaunchSuccessful(output)) {
                    return@withContext LaunchResult.Success
                }
                LaunchResult.Error(R.string.starting_activity_root_error)
            } catch (e: Exception) {
                Timber.e(e)
                LaunchResult.Error(R.string.starting_activity_root_error)
            }
        }

    private fun isLaunchSuccessful(output: String?): Boolean {
        if (output.isNullOrBlank()) {
            return false
        }
        val lower = output.lowercase(Locale.US)
        if (lower.contains("error") ||
            lower.contains("exception") ||
            lower.contains("does not exist") ||
            lower.contains("not started") ||
            lower.contains("permission denial")
        ) {
            return false
        }
        return lower.contains("starting: intent") || lower.contains("activity:")
    }
}