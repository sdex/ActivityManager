package com.sdex.activityrunner.shortcut

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle

import com.sdex.activityrunner.util.RunActivityTask

class ShortcutHandlerActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val packageName = intent.getStringExtra(ARG_PACKAGE_NAME)
    val className = intent.getStringExtra(ARG_CLASS_NAME)
    if (packageName != null && className != null) {
      val componentName = ComponentName(packageName, className)
      val task = RunActivityTask(componentName)
      task.execute()
    }
    finish()
  }

  companion object {

    const val ARG_PACKAGE_NAME = "arg_package_name"
    const val ARG_CLASS_NAME = "arg_class_name"
  }
}
