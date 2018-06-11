package com.sdex.activityrunner.intent.history

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.sdex.activityrunner.db.history.HistoryDatabase
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.commons.ads.AppPreferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

  private val database: HistoryDatabase = HistoryDatabase.getDatabase(application)
  private val appPreferences: AppPreferences = AppPreferences(application)

  val history: LiveData<List<HistoryModel>>
    get() {
      val limit = if (appPreferences.isProVersion) Integer.MAX_VALUE else MAX_FREE_RECORDS
      return database.historyModelDao.getHistory(limit)
    }

  fun deleteItem(model: HistoryModel) {
    async(CommonPool) {
      database.historyModelDao.delete(model)
    }
  }

  fun clear() {
    async(CommonPool) {
      database.historyModelDao.clean()
    }
  }

  companion object {

    const val MAX_FREE_RECORDS = 20
  }
}
