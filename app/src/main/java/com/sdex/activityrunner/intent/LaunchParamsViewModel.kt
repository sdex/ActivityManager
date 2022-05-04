package com.sdex.activityrunner.intent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.db.history.HistoryRepository
import com.sdex.activityrunner.intent.converter.LaunchParamsToHistoryConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LaunchParamsViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
) : ViewModel() {

    fun addToHistory(launchParams: LaunchParams) {
        viewModelScope.launch(Dispatchers.IO) {
            val historyConverter = LaunchParamsToHistoryConverter(launchParams)
            val historyModel = historyConverter.convert()
            historyRepository.insert(historyModel)
        }
    }
}
