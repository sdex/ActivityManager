package com.sdex.activityrunner.shortcut

import android.content.ComponentName
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.sdex.activityrunner.app.ActivityLauncher
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ShortcutHandlerActivity : ComponentActivity() {

    @Inject
    lateinit var activityLauncher: ActivityLauncher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageName = intent.getStringExtra(ARG_PACKAGE_NAME)
        val className = intent.getStringExtra(ARG_CLASS_NAME)
        Timber.d("Shortcut: packageName=$packageName, className=$className")
        if (packageName != null && className != null) {
            val componentName = ComponentName(packageName, className)
            // keep it to support shortcuts created before #56
            val requiresElevation = if (intent.hasExtra(ARG_EXPORTED)) {
                !intent.getBooleanExtra(ARG_EXPORTED, false)
            } else {
                intent.getBooleanExtra(ARG_USE_ROOT, false)
            }
            activityLauncher.launch(componentName, requiresElevation)
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
