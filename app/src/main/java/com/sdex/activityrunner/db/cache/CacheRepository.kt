package com.sdex.activityrunner.db.cache

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

interface CacheRepository {
    fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>>
    suspend fun getApplications(packages: Set<String>?): List<ApplicationModel>
    suspend fun getApplicationPackageNames(): List<String>
    suspend fun upsert(models: List<ApplicationModel>)
    suspend fun delete(models: List<ApplicationModel>): Int
    suspend fun getApplication(packageName: String): ApplicationModel?
    suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int
    suspend fun count(): Int
}
