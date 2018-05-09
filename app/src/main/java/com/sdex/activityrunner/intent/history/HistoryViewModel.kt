package com.sdex.activityrunner.intent.history

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.os.AsyncTask

import com.sdex.activityrunner.db.AppDatabase
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.commons.ads.AppPreferences

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

  private val database: AppDatabase = AppDatabase.getDatabase(application)
  private val appPreferences: AppPreferences = AppPreferences(application)

  val history: LiveData<List<HistoryModel>>
    get() {
      val limit = if (appPreferences.isProVersion) Integer.MAX_VALUE else MAX_FREE_RECORDS
      return database.historyModelDao.getHistory(limit)
    }

  fun deleteItem(model: HistoryModel) {
    DeleteTask(database).execute(model)
  }

  fun clear() {
    ClearTask(database).execute()
  }

  private class DeleteTask internal constructor(private val database: AppDatabase) : AsyncTask<HistoryModel, Void, Void>() {

    override fun doInBackground(vararg params: HistoryModel): Void? {
      database.historyModelDao.delete(*params)
      return null
    }
  }

  private class ClearTask internal constructor(private val database: AppDatabase) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void): Void? {
      database.historyModelDao.clean()
      return null
    }
  }

  companion object {

    const val MAX_FREE_RECORDS = 20
  }
}
