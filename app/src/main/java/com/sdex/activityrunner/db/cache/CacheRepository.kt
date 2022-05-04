package com.sdex.activityrunner.db.cache

import androidx.sqlite.db.SupportSQLiteQuery
import javax.inject.Inject

class CacheRepository @Inject constructor(
    private val applicationModelDao: ApplicationModelDao
) {

    fun getApplications(query: SupportSQLiteQuery) =
        applicationModelDao.getApplicationModels(query)
}
