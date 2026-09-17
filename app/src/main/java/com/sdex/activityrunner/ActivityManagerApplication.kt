package com.sdex.activityrunner

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatDelegate
import com.sdex.activityrunner.app.launcher.ShizukuLaunchStrategy
import com.sdex.activityrunner.intent.analyzer.IntentInterceptor
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class ActivityManagerApplication : Application() {

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate() {
        super.onCreate()
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Timber.plant(Timber.DebugTree())
        }
        appPreferences.onAppOpened()
        AppCompatDelegate.setDefaultNightMode(appPreferences.theme)
        // The component state and the preference can drift apart, e.g. after a backup restore.
        IntentInterceptor.sync(this, appPreferences.isInterceptIntents)
        // Restore the user's assistant setting if a previous Shizuku launch was interrupted.
        ShizukuLaunchStrategy.restorePendingBackup(this, appPreferences)
    }
}
