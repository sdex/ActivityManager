package com.sdex.activityrunner.intent.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    val list: LiveData<PagedList<HistoryModel>>

    init {
        val factory = historyRepository.getHistory()
        val config = PagedList.Config.Builder()
            .setPageSize(50)
            .setEnablePlaceholders(true)
            .build()
        list = LivePagedListBuilder(factory, config).build()
    }

    fun deleteItem(model: HistoryModel) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.delete(model)
        }
    }

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.clean()
        }
    }
}
