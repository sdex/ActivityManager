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
        val syncReason = if (isQuickSyncSupported) {
            val lastSequenceNumber = preferences.lastSequenceNumber
            val lastBootCount = preferences.lastBootCount
            val currentBootCount = environmentInfoProvider.bootCount
            if (lastSequenceNumber == -1) {
                "Initial sync"
            } else if (currentBootCount != lastBootCount) {
                "Boot count changed: $lastBootCount -> $currentBootCount"
            } else {
                val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
                if (changedPackages != null) {
                    "Sequence number changed: $lastSequenceNumber -> ${changedPackages.sequenceNumber}, \n" +
                        "Changed packages: ${changedPackages.packageNames.joinToString()}"
                } else if (hasStaleCachedApplications()) {
                    "Cache contains applications that are no longer installed"
                } else {
                    null
                }
            }
        } else {
            // always perform a full sync on API below 26
            "Quick sync is not supported"
        }
        Timber.d("Sync reason: $syncReason")
        return (syncReason != null)
    }

    suspend fun sync() {
        Timber.d("Sync")
        val targetPackages = getTargetPackages()
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

        updateLastSyncState()
    }

    private fun getTargetPackages(): Set<String>? =
        if (isQuickSyncSupported && preferences.lastSequenceNumber != -1) {
            packageInfoProvider.getChangedPackages(
                preferences.lastSequenceNumber,
            )?.packageNames?.toSet()
        } else {
            null
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

    private fun updateLastSyncState() {
        if (isQuickSyncSupported) {
            val lastSequenceNumber = preferences.lastSequenceNumber
            val lastBootCount = preferences.lastBootCount
            val currentBootCount = environmentInfoProvider.bootCount
            if (lastSequenceNumber == -1 || currentBootCount != lastBootCount) {
                // Sequence numbers reset to 0 whenever the device reboots.
                preferences.lastBootCount = currentBootCount
                val currentSequenceNumber = packageInfoProvider.getChangedPackages(0)
                    ?.sequenceNumber ?: 0
                preferences.lastSequenceNumber = currentSequenceNumber
                Timber.d("Update sync state: bootCount=$currentBootCount, sequence=$currentSequenceNumber")
            } else {
                val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
                if (changedPackages != null) {
                    val currentSequenceNumber = changedPackages.sequenceNumber
                    preferences.lastSequenceNumber = currentSequenceNumber
                    Timber.d("Update sequence number: $currentSequenceNumber -> $currentSequenceNumber")
                }
            }
        }
    }
}
