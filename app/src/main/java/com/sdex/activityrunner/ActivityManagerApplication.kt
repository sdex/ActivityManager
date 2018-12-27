package com.sdex.activityrunner

import android.app.Application
import android.util.Log
import com.flurry.android.FlurryAgent

class ActivityManagerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    FlurryAgent.Builder()
      .withIncludeBackgroundSessionsInMetrics(false)
      .withCaptureUncaughtExceptions(!BuildConfig.DEBUG)
      .withLogEnabled(BuildConfig.DEBUG)
      .withLogLevel(Log.VERBOSE)
      .build(this, "HMVT99FMRW22SXSSRQRQ")
  }
}
