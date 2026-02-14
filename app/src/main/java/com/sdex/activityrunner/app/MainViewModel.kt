package com.sdex.activityrunner.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.ApplicationsLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val appPreferences: AppPreferences,
    private val applicationsLoader: ApplicationsLoader,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    private val _searchQuery = MutableLiveData<String?>(null)
    val searchQuery: LiveData<String?> = _searchQuery

    private val _isSyncing = MutableLiveData(false)
    val isSyncing: LiveData<Boolean> = _isSyncing

    val items: LiveData<List<ApplicationModel>> = searchQuery.switchMap { text ->
        val query = GetApplicationsQuery(appPreferences, text)
        Timber.d("Query: $query")
        cacheRepository.getApplications(query.sqLiteQuery)
    }

    init {
        syncDatabase()
    }

    fun search(text: String?) {
        Timber.d("Search: $text")
        _searchQuery.value = text
    }

    fun refresh() {
        search(searchQuery.value)
    }

    private fun syncDatabase() {
        coroutineScope.launch {
            val shouldSync = applicationsLoader.shouldSync()
            if (shouldSync) {
                if (cacheRepository.count() > 0) {
                    // set isSyncing to true only if there are applications in the database
                    // the app shouldn't show this progress during the initial sync
                    _isSyncing.postValue(true)
                }

                applicationsLoader.sync()

                _isSyncing.postValue(false)
            }
        }
    }
}
