package com.sdex.commons.analytics

import com.flurry.android.FlurryAgent
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.db.cache.ApplicationModel

object AnalyticsManager {

  fun logApplicationOpen(model: ApplicationModel) {
    FlurryAgent.logEvent("application_open", mapOf(
      "package_name" to model.packageName,
      "app_name" to model.name
    ))
  }

  fun logActivityOpen(model: ActivityModel,
                      withParams: Boolean = false,
                      withRoot: Boolean = false) {
    FlurryAgent.logEvent("activity_open", mapOf(
      "package_name" to model.packageName,
      "class_name" to model.className,
      "name" to model.name,
      "exported" to model.exported.toString(),
      "with_params" to withParams.toString(),
      "with_root" to withRoot.toString()
    ))
  }

  fun logCreateShortcut(model: ActivityModel) {
    FlurryAgent.logEvent("create_shortcut", mapOf(
      "package_name" to model.packageName,
      "class_name" to model.className,
      "name" to model.name,
      "exported" to model.exported.toString()
    ))
  }
}
