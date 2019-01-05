package com.sdex.activityrunner.util

import android.content.ComponentName
import android.os.AsyncTask
import com.sdex.commons.analytics.AM_START_ACTIVITY
import com.sdex.commons.analytics.AnalyticsManager

class RunActivityTask(private val componentName: ComponentName) : AsyncTask<Void, Void, Int>() {

  override fun doInBackground(params: Array<Void>): Int? {
    var className = componentName.className
    if (className.contains("$")) {
      className = className.replace("$", "\\$")
    }

    try {
      val command = "am start -n " + componentName.packageName + "/" + className
      RootUtils.execute(command)
    } catch (e: Exception) {
      AnalyticsManager.logError(AM_START_ACTIVITY, componentName.packageName, e.message)
      e.printStackTrace()
    }

    return 0
  }
}
