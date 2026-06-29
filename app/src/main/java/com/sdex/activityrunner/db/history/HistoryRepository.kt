package com.sdex.activityrunner.db.history

import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyModelDao: HistoryModelDao,
) {

    suspend fun delete(vararg model: HistoryModel) {
        historyModelDao.delete(*model)
    }

    suspend fun clean() {
        historyModelDao.clean()
    }

    fun getHistory() = historyModelDao.getHistory()

    suspend fun insert(vararg historyModel: HistoryModel) {
        historyModelDao.insert(*historyModel)
    }
}
