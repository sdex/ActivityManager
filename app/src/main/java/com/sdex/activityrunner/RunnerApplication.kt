package com.sdex.activityrunner

import android.app.Application

class RunnerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
//      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .penaltyDeath()
//        .build())
//      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
//        .detectAll()
//        .penaltyLog()
//        .build())
    }
  }
}
