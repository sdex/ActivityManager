package com.sdex.activityrunner.db.history

import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyModelDao: HistoryModelDao
) {

    fun delete(vararg model: HistoryModel) {
        historyModelDao.delete(*model)
    }

    fun clean() {
        historyModelDao.clean()
    }

    fun getHistory() = historyModelDao.getHistory()

    fun insert(vararg historyModel: HistoryModel) {
        historyModelDao.insert(*historyModel)
    }
}