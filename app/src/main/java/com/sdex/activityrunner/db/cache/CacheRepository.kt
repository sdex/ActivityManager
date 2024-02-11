package com.sdex.activityrunner.db.cache

import androidx.sqlite.db.SupportSQLiteQuery
import javax.inject.Inject

class CacheRepository @Inject constructor(
    private val applicationModelDao: ApplicationModelDao,
) {

    fun getApplications(query: SupportSQLiteQuery) =
        applicationModelDao.getApplicationModels(query)

    fun getApplications() = applicationModelDao.getApplicationModels()

    fun insert(models: List<ApplicationModel>) = applicationModelDao.insert(models)

    fun update(models: List<ApplicationModel>) = applicationModelDao.update(models)

    fun delete(models: List<ApplicationModel>) = applicationModelDao.delete(models)

    fun getApplication(packageName: String) = applicationModelDao.getApplicationModel(packageName)
}
