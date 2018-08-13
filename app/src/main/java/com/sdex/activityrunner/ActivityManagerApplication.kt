package com.sdex.activityrunner

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.support.multidex.MultiDex

class ActivityManagerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build())
      StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
        .detectAll()
        .penaltyLog()
        .build())
    }
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

}
