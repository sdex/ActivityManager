package com.sdex.activityrunner.shortcut

import android.app.Activity
import android.content.ComponentName
import android.os.Bundle
import com.sdex.activityrunner.app.launchActivity
import timber.log.Timber

class ShortcutHandlerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(ARG_PACKAGE_NAME)
        val className = intent.getStringExtra(ARG_CLASS_NAME)
        Timber.d("Shortcut: packageName=$packageName, className=$className")
        if (packageName != null && className != null) {
            val componentName = ComponentName(packageName, className)
            // keep it to support shortcuts created before #56
            if (intent.hasExtra(ARG_EXPORTED)) {
                val isExported = intent.getBooleanExtra(ARG_EXPORTED, false)
                launchActivity(componentName, useRoot = !isExported)
            } else {
                val useRoot = intent.getBooleanExtra(ARG_USE_ROOT, false)
                launchActivity(componentName, useRoot = useRoot)
            }
        }
        finishAffinity()
    }

    companion object {

        const val ARG_PACKAGE_NAME = "arg_package_name"
        const val ARG_CLASS_NAME = "arg_class_name"
        const val ARG_EXPORTED = "arg_exported"
        const val ARG_USE_ROOT = "arg_use_root"
    }
}
