package com.sdex.activityrunner.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.intent.converter.HistoryToLaunchParamsConverter
import com.sdex.activityrunner.intent.converter.LaunchParamsToIntentConverter
import com.sdex.activityrunner.shortcut.ShortcutHandlerActivity
import timber.log.Timber

fun ActivityModel.makeShortcutIntent(
    context: Context,
    useRoot: Boolean = !exported,
) = toIntent(context).apply {
    putExtra(ShortcutHandlerActivity.ARG_USE_ROOT, useRoot)
}

fun HistoryModel.makeShortcutIntent(): Intent {
    val historyToLaunchParamsConverter = HistoryToLaunchParamsConverter(this)
    val launchParams = historyToLaunchParamsConverter.convert()
    val converter = LaunchParamsToIntentConverter(launchParams)
    return converter.convert()
}

private fun getActivityIntent(action: String? = null, component: ComponentName): Intent {
    return Intent().apply {
        this.action = action
        this.component = component
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
}

private fun ActivityModel.toIntent(context: Context): Intent {
    val component = ComponentName(
        context.packageName,
        ShortcutHandlerActivity::class.java.canonicalName!!,
    )
    val intent = getActivityIntent(Intent.ACTION_VIEW, component)
    intent.putExtra(ShortcutHandlerActivity.ARG_PACKAGE_NAME, this.packageName)
    intent.putExtra(ShortcutHandlerActivity.ARG_CLASS_NAME, this.className)
    return intent
}

object IntentUtils {

    fun launchActivity(
        context: Context,
        component: ComponentName,
        name: String,
        showMessage: Boolean = true,
    ) {
        try {
            val intent = getActivityIntent(component = component)
            context.startActivity(intent)
            if (showMessage) {
                Toast.makeText(
                    context,
                    context.getString(R.string.starting_activity, name),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        } catch (e: SecurityException) {
            Timber.e(e)
            Toast.makeText(
                context,
                context.getString(R.string.starting_activity_failed_security, name),
                Toast.LENGTH_SHORT,
            ).show()
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(
                context,
                context.getString(R.string.starting_activity_failed, name),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun launchActivity(
        context: Context,
        intent: Intent,
    ) {
        try {
            context.startActivity(intent)
            Toast.makeText(context, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.starting_activity_intent_failed)
                .setMessage(e.message)
                .setPositiveButton(android.R.string.ok, null)
                .show().apply {
                    val messageTextView = findViewById<TextView>(android.R.id.message)
                    messageTextView?.setTextIsSelectable(true)
                }
        }
    }

    fun launchApplication(
        context: Context,
        packageName: String,
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            launchActivity(context, intent)
        } else {
            Toast.makeText(
                context, R.string.starting_activity_launch_intent_failed,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    fun openApplicationInfo(
        context: Context,
        packageName: String,
    ) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = "package:$packageName".toUri()
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context.startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(
                    context, R.string.starting_activity_intent_failed,
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun openBrowser(
        context: Context,
        url: String,
    ) {
        try {
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()
            customTabsIntent.launchUrl(context, url.toUri())
        } catch (_: Exception) {
            AppUtils.openLink(context, url)
        }
    }
}
