package com.sdex.activityrunner.intent.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sdex.activityrunner.db.history.HistoryDatabase
import com.sdex.activityrunner.db.history.HistoryModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

  private val database: HistoryDatabase = HistoryDatabase.getDatabase(application)
  val list: LiveData<PagedList<HistoryModel>>

  init {
    val factory: DataSource.Factory<Int, HistoryModel> = database.historyModelDao.getHistory()
    val config = PagedList.Config.Builder()
      .setPageSize(50)
      .setEnablePlaceholders(true)
      .build()
    list = LivePagedListBuilder<Int, HistoryModel>(factory, config).build()
  }

  fun deleteItem(model: HistoryModel) {
    GlobalScope.launch {
      database.historyModelDao.delete(model)
    }
  }

  fun clear() {
    GlobalScope.launch {
      database.historyModelDao.clean()
    }
  }
}
