package com.sdex.activityrunner.intent

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.AsyncTask
import com.sdex.activityrunner.db.AppDatabase
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter

class LaunchParamsViewModel(application: Application) : AndroidViewModel(application) {

  private val appDatabase: AppDatabase = AppDatabase.getDatabase(application)

  fun addToHistory(launchParams: LaunchParams) {
    InsertAsyncTask(appDatabase).execute(launchParams)
  }

  private class InsertAsyncTask internal constructor(private val database: AppDatabase)
    : AsyncTask<LaunchParams, Void, Void>() {

    override fun doInBackground(vararg params: LaunchParams): Void? {
      val historyConverter = LaunchParamsToHistoryConverter(params[0])
      val historyModel = historyConverter.convert()
      database.historyModelDao.insert(historyModel)
      return null
    }
  }

}
