package com.sdex.activityrunner.db.cache

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CacheRepository @Inject constructor(
    private val applicationModelDao: ApplicationModelDao,
) {

    fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> =
        applicationModelDao.getApplicationModels(query)

    suspend fun getApplications(packages: Set<String>?) =
        if (packages.isNullOrEmpty()) {
            applicationModelDao.getApplicationModels()
        } else {
            applicationModelDao.getApplicationModels(packages)
        }

    suspend fun upsert(models: List<ApplicationModel>) = applicationModelDao.upsert(models)

    suspend fun delete(models: List<ApplicationModel>) = applicationModelDao.delete(models)

    suspend fun getApplication(packageName: String) = applicationModelDao.getApplicationModel(packageName)

    suspend fun count() = applicationModelDao.count()
}
