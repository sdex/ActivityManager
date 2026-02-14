package com.sdex.activityrunner.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.preferences.AppPreferences
import timber.log.Timber

class ApplicationsLoader(
    private val context: Context,
    private val cacheRepository: CacheRepository,
    private val packageInfoProvider: PackageInfoProvider,
    private val preferences: AppPreferences,
) {

    fun sync() {
        val oldList = cacheRepository.getApplications()
        val newList = getApplicationsList()

        val listToDelete = oldList.toMutableList().also { it.removeAll(newList) }
        val listToInsert = newList.toMutableList().also { it.removeAll(oldList) }
        val listToUpdate = oldList.intersect(newList.toSet()).toList()

        if (listToDelete.isNotEmpty()) {
            val count = cacheRepository.delete(listToDelete)
            Timber.d("Deleted $count records")
        }

        if (listToInsert.isNotEmpty()) {
            val ids = cacheRepository.insert(listToInsert)
            Timber.d("Inserted ${ids.size} records")
        }

        if (listToUpdate.isNotEmpty()) {
            val count = cacheRepository.update(listToUpdate)
            Timber.d("Updated $count records")
        }

        updateLastSyncState()
    }

    private fun getApplicationsList(): List<ApplicationModel> {
        return packageInfoProvider.getInstalledPackages()
            .mapNotNull { packageInfoProvider.getApplication(it) }
    }

    private fun updateLastSyncState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val lastSequenceNumber = preferences.lastSequenceNumber
            val lastBootCount = preferences.lastBootCount

            val currentBootCount = getCurrentBootCount(context)

            Timber.d("Last sequence number: $lastSequenceNumber")
            Timber.d("Last boot count: $lastBootCount, current boot count: $currentBootCount")

            if (currentBootCount != lastBootCount) {
                // Sequence numbers reset to 0 whenever the device reboots.
                preferences.lastBootCount = currentBootCount
                preferences.lastSequenceNumber = 0
            } else {
                val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
                if (changedPackages != null) {
                    val currentSequenceNumber = changedPackages.sequenceNumber
                    preferences.lastSequenceNumber = currentSequenceNumber
                    Timber.d("Current sequence number: $currentSequenceNumber")
                }
            }
        }
    }

    fun shouldSync(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val lastSequenceNumber = preferences.lastSequenceNumber
            val lastBootCount = preferences.lastBootCount
            val currentBootCount = getCurrentBootCount(context)
            if (currentBootCount != lastBootCount) {
                true
            } else {
                val changedPackages = packageInfoProvider.getChangedPackages(lastSequenceNumber)
                changedPackages != null
            }
        } else {
            // always perform a full sync on API below 26
            true
        }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getCurrentBootCount(context: Context): Int {
        val currentBootCount = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.BOOT_COUNT,
            0,
        )
        return currentBootCount
    }
}
