package com.sdex.activityrunner.service

import androidx.work.Worker

class ApplicationsListWorker : Worker() {

  override fun doWork(): Result {
    val loader = ApplicationListLoader()
    loader.syncDatabase(applicationContext)
    return Result.SUCCESS
  }

  companion object {
    const val TAG = "ApplicationsListWorker"
  }
}
