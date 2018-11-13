package com.sdex.activityrunner.intent.history

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.sdex.activityrunner.db.history.HistoryDatabase
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.preferences.AppPreferences
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

  private val database: HistoryDatabase = HistoryDatabase.getDatabase(application)
  private val appPreferences: AppPreferences = AppPreferences(application)
  val list: LiveData<PagedList<HistoryModel>>

  init {
    val limit = if (appPreferences.isProVersion) Integer.MAX_VALUE else MAX_FREE_RECORDS
    val factory: DataSource.Factory<Int, HistoryModel> = database.historyModelDao.getHistory(limit)
    val config = PagedList.Config.Builder()
      .setPageSize(50)
      .setEnablePlaceholders(true)
      .build()
    list = LivePagedListBuilder<Int, HistoryModel>(factory, config).build()
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
