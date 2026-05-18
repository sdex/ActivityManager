package com.sdex.activityrunner.db.cache

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

class CacheRepositoryImpl(
    private val applicationModelDao: ApplicationModelDao,
) : CacheRepository {

    override fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> =
        applicationModelDao.getApplicationModels(query)

    override suspend fun getApplications(packages: Set<String>?): List<ApplicationModel> =
        if (packages.isNullOrEmpty()) {
            applicationModelDao.getApplicationModels()
        } else {
            applicationModelDao.getApplicationModels(packages)
        }

    override suspend fun getApplicationPackageNames(): List<String> =
        applicationModelDao.getApplicationPackageNames()

    override suspend fun upsert(models: List<ApplicationModel>) =
        applicationModelDao.upsert(models)

    override suspend fun delete(models: List<ApplicationModel>): Int =
        applicationModelDao.delete(models)

    override suspend fun getApplication(packageName: String): ApplicationModel? =
        applicationModelDao.getApplicationModel(packageName)

    override suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int =
        applicationModelDao.updatePinnedAt(packageName, pinnedAt)

    override suspend fun count(): Int = applicationModelDao.count()
}
