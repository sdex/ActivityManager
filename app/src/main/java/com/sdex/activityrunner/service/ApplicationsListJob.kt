package com.sdex.activityrunner.service

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService

class ApplicationsListJob : JobIntentService() {

  override fun onHandleWork(intent: Intent) {
    val loader = ApplicationListLoader()
    loader.syncDatabase(applicationContext)
  }

  companion object {

    private const val JOB_ID = 1212

    fun enqueueWork(context: Context, work: Intent) {
      JobIntentService.enqueueWork(context, ApplicationsListJob::class.java, JOB_ID, work)
    }
  }
}
