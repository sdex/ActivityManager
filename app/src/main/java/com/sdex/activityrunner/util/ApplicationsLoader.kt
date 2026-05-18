package com.sdex.activityrunner.util

import com.sdex.activityrunner.commons.platform.EnvironmentInfoProvider
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.preferences.AppPreferences
import timber.log.Timber

class ApplicationsLoader(
    private val cacheRepository: CacheRepository,
    private val packageInfoProvider: PackageInfoProvider,
    private val preferences: AppPreferences,
    private val environmentInfoProvider: EnvironmentInfoProvider,
) {

    val isQuickSyncSupported: Boolean = environmentInfoProvider.isQuickSyncSupported

    suspend fun shouldSync(): Boolean {
        val syncTrigger = if (isQuickSyncSupported) {
            val lastSequenceNumber = preferences.lastSequenceNumber
            val lastBootCount = preferences.lastBootCount
            val currentBootCount = environmentInfoProvider.bootCount
            if (lastSequenceNumber == -1) {
                "Initial sync"
            } else if (currentBootCount != lastBootCount) {
                "Boot count changed: $lastBootCount->$currentBootCount"
            } else {
                val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
                if (changedPackages != null) {
                    "Sequence number changed: $lastSequenceNumber->${changedPackages.sequenceNumber}, \n" +
                        "Changed packages: ${changedPackages.packageNames.joinToString()}"
                } else if (hasStaleCachedApplications()) {
                    "Cache contains applications that are no longer installed"
                } else {
                    null
                }
            }
        } else {
            // always perform a full sync on API below 26
            "Initial sync, quick sync is not supported"
        }
        Timber.d("Sync trigger: $syncTrigger")
        return (syncTrigger != null)
    }

    suspend fun sync() {
        val syncPlan = getSyncPlan()
        Timber.d("Sync: $syncPlan")

        val targetPackages = syncPlan.targetPackages
        val newList = getApplicationsList(targetPackages)
        val oldList = cacheRepository.getApplications(targetPackages)

        val oldMap = oldList.associateBy { it.packageName }
        val newMap = newList.associateBy { it.packageName }

        val listToDelete = oldList.filterNot { newMap.containsKey(it.packageName) }
        // preserve pinned state
        val listToSave = newList.map { newApp ->
            val oldApp = oldMap[newApp.packageName]
            newApp.copy(pinnedAt = oldApp?.pinnedAt ?: 0)
        }.filter { newApp ->
            val oldApp = oldMap[newApp.packageName]
            // save if it's not in the database OR if the metadata differs
            oldApp == null || oldApp != newApp
        }

        if (listToDelete.isNotEmpty()) {
            cacheRepository.delete(listToDelete)
            Timber.d("Removed ${listToDelete.size} apps")
        }

        if (listToSave.isNotEmpty()) {
            cacheRepository.upsert(listToSave)
            Timber.d("Saved (Insert/Update) ${listToSave.size} apps")
        }

        updateLastSyncState(syncPlan.syncState)
    }

    private fun getSyncPlan(): SyncPlan {
        if (!isQuickSyncSupported) {
            return SyncPlan(targetPackages = null, syncState = null)
        }

        val lastSequenceNumber = preferences.lastSequenceNumber
        val currentBootCount = environmentInfoProvider.bootCount
        if (lastSequenceNumber == -1 || currentBootCount != preferences.lastBootCount) {
            // Sequence numbers reset to 0 whenever the device reboots.
            val currentSequenceNumber = packageInfoProvider.getChangedPackages(0)
                ?.sequenceNumber ?: 0
            return SyncPlan(
                targetPackages = null,
                syncState = SyncState(
                    bootCount = currentBootCount,
                    sequenceNumber = currentSequenceNumber,
                ),
            )
        }

        val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
        return SyncPlan(
            targetPackages = changedPackages?.packageNames?.toSet(),
            syncState = changedPackages?.let {
                SyncState(
                    bootCount = currentBootCount,
                    sequenceNumber = it.sequenceNumber,
                )
            },
        )
    }

    private fun getApplicationsList(
        packages: Set<String>?,
    ): List<ApplicationModel> {
        return packageInfoProvider.getInstalledPackages()
            .filter { packages == null || packages.contains(it) }
            .mapNotNull { packageInfoProvider.getApplication(it) }
    }

    private suspend fun hasStaleCachedApplications(): Boolean {
        val installedPackages = packageInfoProvider.getInstalledPackages().toSet()
        return cacheRepository.getApplicationPackageNames()
            .any { it !in installedPackages }
    }

    private fun updateLastSyncState(syncState: SyncState?) {
        if (syncState != null) {
            val previousBootCount = preferences.lastBootCount
            val previousSequenceNumber = preferences.lastSequenceNumber
            preferences.lastBootCount = syncState.bootCount
            preferences.lastSequenceNumber = syncState.sequenceNumber
            Timber.d(
                "Update sync state: bootCount=$previousBootCount->${syncState.bootCount}, " +
                    "sequence=$previousSequenceNumber->${syncState.sequenceNumber}",
            )
        }
    }

    private data class SyncPlan(
        val targetPackages: Set<String>?,
        val syncState: SyncState?,
    )

    private data class SyncState(
        val bootCount: Int,
        val sequenceNumber: Int,
    )
}
