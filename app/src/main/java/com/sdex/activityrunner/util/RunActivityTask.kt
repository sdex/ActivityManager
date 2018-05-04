package com.sdex.activityrunner.util

import android.content.ComponentName
import android.os.AsyncTask
import com.topjohnwu.superuser.Shell

class RunActivityTask(private val componentName: ComponentName) : AsyncTask<Void, Void, Int>() {

  override fun doInBackground(params: Array<Void>): Int? {
    var className = componentName.className
    if (className.contains("$")) {
      className = className.replace("$", "\\$")
    }

    try {
      val command = "am start -n " + componentName.packageName + "/" + className
      Shell.Sync.su(command)
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return 0
  }
}