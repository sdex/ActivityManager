package com.sdex.activityrunner.app

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.widget.Toast
import com.sdex.activityrunner.R
import com.sdex.activityrunner.intent.IntentBuilderActivity
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

fun Activity.launchActivity(
    model: ActivityModel,
    useRoot: Boolean = false,
    useParams: Boolean = false
) {
    if (useParams) {
        IntentBuilderActivity.start(this, model)
    } else if (!model.exported || useRoot) {
        launchActivityWithRoot(this, model.componentName)
    } else {
        IntentUtils.launchActivity(this, model.componentName, model.name)
    }
}

fun Activity.launchActivity(
    componentName: ComponentName,
    useRoot: Boolean,
) {
    if (useRoot) {
        launchActivityWithRoot(this, componentName)
    } else {
        IntentUtils.launchActivity(
            this,
            componentName,
            componentName.className.split(".").last(),
            false
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun launchActivityWithRoot(
    context: Context,
    componentName: ComponentName
) {
    GlobalScope.launch(Dispatchers.IO) {
        when (launchActivityUsingRoot(componentName)) {
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

private fun launchActivityUsingRoot(componentName: ComponentName): Int {
    return if (!RootUtils.isSuAvailable()) {
        ROOT_NOT_AVAILABLE
    } else {
        try {
            val command = "am start -n " + componentName.packageName + "/" +
                componentName.normalizeClassName()
            Timber.d("Execute: \"$command\"")
            val result = RootUtils.execute(command)
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
