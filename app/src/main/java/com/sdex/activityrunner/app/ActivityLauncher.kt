package com.sdex.activityrunner.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.launcher.LaunchMethod
import com.sdex.activityrunner.app.launcher.LaunchResult
import com.sdex.activityrunner.app.launcher.LaunchStrategy
import com.sdex.activityrunner.app.launcher.LaunchStrategyFactory
import com.sdex.activityrunner.onboarding.ShizukuOnboardingActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.IntentUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point for launching activities. This is the only place that decides *how* to launch:
 * exported activities go through a plain `startActivity`, while non-exported (or permission-guarded)
 * ones are elevated via root or Shizuku (whichever is available), falling back to the Shizuku setup
 * screen when neither is.
 */
@Singleton
class ActivityLauncher @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val strategyFactory: LaunchStrategyFactory,
    private val coroutineScope: CoroutineScope,
    private val appPreferences: AppPreferences,
) {

    fun launch(model: ActivityModel) {
        launch(model.componentName, model.name, model.launchRequiresRoot)
    }

    fun launch(component: ComponentName, requiresElevation: Boolean) {
        launch(component, component.shortName(), requiresElevation)
    }

    /**
     * Launches an already-built [intent] in-process (e.g. from the intent builder). Runs from the
     * application context, so [Intent.FLAG_ACTIVITY_NEW_TASK] is enforced. Returns `null` on
     * success, or the failure message on error so the caller can surface it with a UI context.
     */
    fun launchIntent(intent: Intent): String? {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            if (appPreferences.isShowLaunchToast) {
                Toast.makeText(context, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show()
            }
            null
        } catch (e: Exception) {
            Timber.e(e)
            e.message ?: context.getString(R.string.starting_activity_intent_failed)
        }
    }

    fun launchWithRoot(model: ActivityModel) {
        coroutineScope.launch {
            runElevated(strategyFactory.get(LaunchMethod.ROOT), model.componentName, model.name)
        }
    }

    private fun launch(component: ComponentName, name: String, requiresElevation: Boolean) {
        if (!requiresElevation) {
            IntentUtils.launchActivity(
                context = context,
                component = component,
                name = name,
                showMessage = appPreferences.isShowLaunchToast,
            )
            return
        }
        coroutineScope.launch {
            val strategy = strategyFactory.resolveAvailable(context)
            if (strategy == null) {
                showOnboarding()
            } else {
                runElevated(strategy, component, name)
            }
        }
    }

    private suspend fun runElevated(
        strategy: LaunchStrategy,
        component: ComponentName,
        name: String,
    ) {
        if (appPreferences.isShowLaunchToast) {
            toast(context.getString(R.string.starting_activity, name))
        }
        when (val result = strategy.launch(context, component)) {
            LaunchResult.Success -> Unit
            LaunchResult.Unavailable ->
                toast(context.getString(R.string.starting_activity_root_not_available))

            is LaunchResult.Error -> toast(context.getString(result.messageRes))
        }
    }

    private suspend fun showOnboarding() = withContext(Dispatchers.Main) {
        val intent = ShizukuOnboardingActivity.intent(context)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private suspend fun toast(message: String) = withContext(Dispatchers.Main) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun ComponentName.shortName(): String = className.substringAfterLast('.')
}
