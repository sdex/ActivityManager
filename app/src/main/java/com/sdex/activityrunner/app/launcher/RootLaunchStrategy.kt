package com.sdex.activityrunner.app.launcher

import android.content.ComponentName
import android.content.Context
import com.sdex.activityrunner.R
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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
                val command =
                    "am start -n ${component.packageName}/${component.normalizeClassName()}"
                Timber.d("Execute: \"$command\"")
                val result = RootUtils.execute(suExecutable, command)
                Timber.d("Result: \"$result\"")
                LaunchResult.Success
            } catch (e: Exception) {
                Timber.e(e)
                LaunchResult.Error(R.string.starting_activity_root_error)
            }
        }

    private fun ComponentName.normalizeClassName(): String =
        if (className.contains("$")) className.replace("$", "\\$") else className
}
