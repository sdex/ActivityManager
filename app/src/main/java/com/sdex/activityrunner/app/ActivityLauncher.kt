package com.sdex.activityrunner.app

import android.content.ComponentName
import android.content.Context
import android.widget.Toast
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.IntentBuilderActivity
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.IntentUtils
import com.sdex.activityrunner.util.RootUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

private const val ROOT_OK = 0
private const val ROOT_ERROR = 1
private const val ROOT_NOT_AVAILABLE = 2

fun Context.launchActivity(
    model: ActivityModel,
    useRoot: Boolean = false,
    useParams: Boolean = false,
) {
    if (useParams) {
        IntentBuilderActivity.start(
            context = this,
            model = model,
        )
    } else if (!model.exported || useRoot) {
        launchActivityWithRoot(
            context = this,
            componentName = model.componentName,
        )
    } else {
        IntentUtils.launchActivity(
            context = this,
            component = model.componentName,
            name = model.name,
        )
    }
}

fun Context.launchActivity(
    componentName: ComponentName,
    useRoot: Boolean,
) {
    if (useRoot) {
        launchActivityWithRoot(
            context = this,
            componentName = componentName,
        )
    } else {
        IntentUtils.launchActivity(
            context = this,
            component = componentName,
            name = componentName.className.split(".").last(),
            showMessage = false,
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun launchActivityWithRoot(
    context: Context,
    componentName: ComponentName,
) {
    val appPreferences = AppPreferences(context)
    val suExecutable = appPreferences.suExecutable
    GlobalScope.launch(Dispatchers.IO) {
        when (launchActivityUsingRoot(suExecutable, componentName)) {
            ROOT_ERROR -> R.string.starting_activity_root_error
            ROOT_NOT_AVAILABLE -> R.string.starting_activity_root_not_available
            else -> null
        }?.let {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}

private fun launchActivityUsingRoot(
    suExecutable: String,
    componentName: ComponentName,
): Int {
    return if (!RootUtils.isSuAvailable(suExecutable)) {
        ROOT_NOT_AVAILABLE
    } else {
        try {
            val command = "am start -n " + componentName.packageName + "/" +
                componentName.normalizeClassName()
            Timber.d("Execute: \"$command\"")
            val result = RootUtils.execute(suExecutable, command)
            Timber.d("Result: \"$result\"")
            ROOT_OK
        } catch (e: Exception) {
            Timber.e(e)
            ROOT_ERROR
        }
    }
}

private fun ComponentName.normalizeClassName(): String =
    if (className.contains("$")) {
        className.replace("$", "\\$")
    } else {
        className
    }
