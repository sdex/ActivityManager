package com.sdex.activityrunner.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v4.content.pm.ShortcutInfoCompat
import android.support.v4.content.pm.ShortcutManagerCompat
import android.support.v4.graphics.drawable.IconCompat
import android.support.v7.app.AlertDialog
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap

import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.R
import com.sdex.activityrunner.db.activity.ActivityModel
import com.sdex.activityrunner.glide.GlideApp
import com.sdex.activityrunner.shortcut.ShortcutHandlerActivity

object IntentUtils {

  private fun getActivityIntent(activity: ComponentName): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.component = activity
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    return intent
  }

  fun createLauncherIcon(context: Context, activityModel: ActivityModel, bitmap: Bitmap?) {
    val componentName: ComponentName = if (activityModel.exported) {
      activityModel.componentName
    } else {
      ComponentName(BuildConfig.APPLICATION_ID, ShortcutHandlerActivity::class.java.canonicalName)
    }

    val intent = getActivityIntent(componentName)

    if (!activityModel.exported) {
      val originComponent = activityModel.componentName
      intent.putExtra(ShortcutHandlerActivity.ARG_PACKAGE_NAME, originComponent.packageName)
      intent.putExtra(ShortcutHandlerActivity.ARG_CLASS_NAME, originComponent.className)
    }

    val iconCompat = IconCompat.createWithBitmap(bitmap)
    createLauncherIcon(context, activityModel.name, intent, iconCompat)
  }

  fun createLauncherIcon(context: Context, name: String, intent: Intent, @DrawableRes icon: Int) {
    val iconCompat = IconCompat.createWithResource(context, icon)
    createLauncherIcon(context, name, intent, iconCompat)
  }

  private fun createLauncherIcon(context: Context, name: String, intent: Intent, icon: IconCompat) {
    val pinShortcutInfo = ShortcutInfoCompat.Builder(context, name)
      .setIcon(icon)
      .setShortLabel(name)
      .setIntent(intent)
      .build()
    ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null)
  }

  fun createLauncherIcon(context: Context, activityModel: ActivityModel) {
    GlideApp.with(context)
      .asDrawable()
      .load(activityModel)
      .error(R.mipmap.ic_launcher)
      .override(100)
      .into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
          createLauncherIcon(context, activityModel, resource.toBitmap())
        }
      })
  }

  fun launchActivity(context: Context, activity: ComponentName, name: String) {
    try {
      val intent = getActivityIntent(activity)
      context.startActivity(intent)
      Toast.makeText(context, context.getString(R.string.starting_activity, name),
        Toast.LENGTH_SHORT).show()
    } catch (e: SecurityException) {
      Toast.makeText(context, context.getString(R.string.starting_activity_failed_security, name),
        Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
      Toast.makeText(context, context.getString(R.string.starting_activity_failed, name),
        Toast.LENGTH_SHORT).show()
    }
  }

  fun launchActivity(context: Context, intent: Intent) {
    try {
      context.startActivity(intent)
      Toast.makeText(context, R.string.starting_activity_intent, Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
      AlertDialog.Builder(context)
        .setTitle(R.string.starting_activity_intent_failed)
        .setMessage(e.message)
        .setPositiveButton(android.R.string.ok, null)
        .show()
    }
  }

  fun openApplicationInfo(context: Context, packageName: String) {
    try {
      val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
      intent.data = Uri.parse("package:$packageName")
      context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
      context.startActivity(intent)
    }
  }
}