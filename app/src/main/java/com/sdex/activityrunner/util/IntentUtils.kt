package com.sdex.activityrunner.util

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.shortcut.ShortcutHandlerActivity
import com.sdex.activityrunner.shortcut.createShortcut
import timber.log.Timber

object IntentUtils {

    private fun getActivityIntent(action: String? = null, component: ComponentName): Intent {
        return Intent().apply {
            this.action = action
            this.component = component
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

    fun createLauncherIcon(
        context: Context,
        activityModel: ActivityModel,
        bitmap: Bitmap?,
        useRoot: Boolean = false,
    ) {
        if (bitmap != null) {
            val intent = activityModel.toIntent(context).apply {
                putExtra(ShortcutHandlerActivity.ARG_USE_ROOT, useRoot)
            }
            val iconCompat = try {
                IconCompat.createWithBitmap(bitmap)
            } catch (e: Exception) { // android.os.TransactionTooLargeException
                Timber.i(e)
                // the icon is too big, fall back to the default icon
                IconCompat.createWithResource(context, R.mipmap.ic_launcher)
            }
            createShortcut(context, activityModel.name, intent, iconCompat)
        } else {
            loadActivityIcon(context, activityModel)
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

    private fun loadActivityIcon(context: Context, activityModel: ActivityModel) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val launcherLargeIconSize = activityManager.launcherLargeIconSize
        Glide.with(context)
            .asDrawable()
            .load(activityModel)
            .error(R.mipmap.ic_launcher)
            .override(launcherLargeIconSize)
            .listener(
                object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        createLauncherIcon(context, activityModel, resource.toBitmap())
                        return false
                    }
                },
            )
            .submit()
    }

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

    fun launchActivity(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
            Toast.makeText(context, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.starting_activity_intent_failed)
                .setMessage(e.message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    fun launchApplication(context: Context, packageName: String) {
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

    fun openApplicationInfo(context: Context, packageName: String) {
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

    fun openBrowser(context: Context, url: String) {
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
