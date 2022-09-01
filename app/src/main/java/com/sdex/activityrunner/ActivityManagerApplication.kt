package com.sdex.activityrunner

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ActivityManagerApplication : Application() {

    private val appPreferences by lazy { AppPreferences(this) }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        appPreferences.onAppOpened()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
