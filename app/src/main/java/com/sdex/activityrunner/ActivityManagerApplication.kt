package com.sdex.activityrunner

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.util.Log
import com.flurry.android.FlurryAgent

class ActivityManagerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
//    if (BuildConfig.DEBUG) {
//      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .build())
//      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .build())
//    }

    FlurryAgent.Builder()
      .withIncludeBackgroundSessionsInMetrics(false)
      .withCaptureUncaughtExceptions(true)
      .withLogEnabled(BuildConfig.DEBUG)
      .withLogLevel(Log.VERBOSE)
      .build(this, "HMVT99FMRW22SXSSRQRQ")
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }
}
