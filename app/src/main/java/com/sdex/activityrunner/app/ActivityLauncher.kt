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
import com.sdex.activityrunner.util.IntentUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
) {

    fun launch(model: ActivityModel) {
        launch(model.componentName, model.name, model.launchRequiresRoot)
    }

    fun launch(component: ComponentName, requiresElevation: Boolean) {
        launch(component, component.shortName(), requiresElevation)
    }

    fun launchWithRoot(model: ActivityModel) {
        coroutineScope.launch {
            runElevated(strategyFactory.get(LaunchMethod.ROOT), model.componentName, model.name)
        }
    }

    private fun launch(component: ComponentName, name: String, requiresElevation: Boolean) {
        if (!requiresElevation) {
            IntentUtils.launchActivity(context, component, name)
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
        toast(context.getString(R.string.starting_activity, name))
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
