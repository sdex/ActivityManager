package com.sdex.commons.analytics

import com.flurry.android.FlurryAgent
import com.sdex.activityrunner.BuildConfig
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel

object AnalyticsManager {

  fun logApplicationOpen(model: ApplicationModel) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent(APPLICATION_OPEN, mapOf(
        "package_name" to model.packageName,
        "app_name" to model.name
      ))
    }
  }

  fun logActivityOpen(model: ActivityModel,
                      withParams: Boolean = false,
                      withRoot: Boolean = false) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent(ACTIVITY_OPEN, mapOf(
        "package_name" to model.packageName,
        "class_name" to model.className,
        "name" to model.name,
        "exported" to model.exported.toString(),
        "with_params" to withParams.toString(),
        "with_root" to withRoot.toString()
      ))
    }
  }

  fun logCreateShortcut(model: ActivityModel) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent(CREATE_SHORTCUT, mapOf(
        "package_name" to model.packageName,
        "class_name" to model.className,
        "name" to model.name,
        "exported" to model.exported.toString()
      ))
    }
  }

  fun logManifestView(model: ApplicationModel) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent(MANIFEST_OPEN, mapOf(
        "package_name" to model.packageName,
        "app_name" to model.name
      ))
    }
  }

  fun logIntentLauncherOpen(model: ActivityModel?) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent(INTENT_LAUNCHER_OPEN, mapOf(
        "package_name" to model?.packageName,
        "class_name" to model?.className,
        "name" to model?.name,
        "exported" to model?.exported.toString()
      ))
    }
  }

  fun logError(name: String, packageName: String? = null, message: String? = null) {
    if (!BuildConfig.DEBUG) {
      FlurryAgent.logEvent("error", mapOf(
        "name" to name,
        "package_name" to packageName,
        "message" to message
      ))
    }
  }
}
