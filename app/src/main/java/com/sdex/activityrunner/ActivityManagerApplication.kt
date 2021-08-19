package com.sdex.activityrunner

import android.app.Application
import timber.log.Timber

class ActivityManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
