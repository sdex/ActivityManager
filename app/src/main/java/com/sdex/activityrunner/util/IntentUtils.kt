package com.sdex.activityrunner.util

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.glide.GlideApp
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

    fun createLauncherIcon(context: Context, activityModel: ActivityModel, bitmap: Bitmap?) {
        if (bitmap != null) {
            val intent = activityModelToIntent(activityModel)
            val iconCompat = IconCompat.createWithBitmap(bitmap)
            try {
                createShortcut(context, activityModel.name, intent, iconCompat)
            } catch (e: Exception) { // android.os.TransactionTooLargeException
                Timber.i(e)
                createLauncherIcon(context, activityModel.name, intent, R.mipmap.ic_launcher)
            }
        } else {
            createLauncherIcon(context, activityModel)
        }
    }

    private fun activityModelToIntent(activityModel: ActivityModel): Intent {
        val component = ComponentName(
            BuildConfig.APPLICATION_ID,
            ShortcutHandlerActivity::class.java.canonicalName!!
        )
        val intent = getActivityIntent(Intent.ACTION_VIEW, component)
        intent.putExtra(ShortcutHandlerActivity.ARG_PACKAGE_NAME, activityModel.packageName)
        intent.putExtra(ShortcutHandlerActivity.ARG_CLASS_NAME, activityModel.className)
        intent.putExtra(ShortcutHandlerActivity.ARG_EXPORTED, activityModel.exported)
        return intent
    }

    fun createLauncherIcon(context: Context, name: String, intent: Intent, @DrawableRes icon: Int) {
        val iconCompat = IconCompat.createWithResource(context, icon)
        createShortcut(context, name, intent, iconCompat)
    }

    private fun createLauncherIcon(context: Context, activityModel: ActivityModel) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val size = am.launcherLargeIconSize
        GlideApp.with(context)
            .asDrawable()
            .load(activityModel)
            .error(R.mipmap.ic_launcher)
            .override(size)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?, dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    createLauncherIcon(context, activityModel, resource?.toBitmap())
                    return false
                }
            })
            .submit()
    }

    fun launchActivity(
        context: Context,
        component: ComponentName,
        name: String,
        showMessage: Boolean = true
    ) {
        try {
            val intent = getActivityIntent(component = component)
            context.startActivity(intent)
            if (showMessage) {
                Toast.makeText(
                    context, context.getString(R.string.starting_activity, name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: SecurityException) {
            Timber.e(e)
            Toast.makeText(
                context, context.getString(R.string.starting_activity_failed_security, name),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Timber.e(e)
            Toast.makeText(
                context, context.getString(R.string.starting_activity_failed, name),
                Toast.LENGTH_SHORT
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
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openApplicationInfo(context: Context, packageName: String) {
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            try {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context, R.string.starting_activity_intent_failed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun openBrowser(context: Context, url: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context, R.color.blue_dark))
            builder.setShowTitle(true)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            AppUtils.openLink(context, url)
        }
    }
}
