package com.sdex.activityrunner.intent.history

import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    val list: Flow<List<HistoryModel>> = historyRepository.getHistory()

    fun deleteItem(model: HistoryModel) = coroutineScope.launch {
        historyRepository.delete(model)
    }

    fun clear() = coroutineScope.launch {
        historyRepository.clean()
    }
}
