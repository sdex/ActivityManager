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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val appPreferences: AppPreferences,
    private val applicationsLoader: ApplicationsLoader,
) : ViewModel() {

    private val _searchQuery = MutableLiveData<String?>(null)
    val searchQuery: LiveData<String?> = _searchQuery

    val items: LiveData<List<ApplicationModel>> = searchQuery.switchMap { text ->
        val query = GetApplicationsQuery(appPreferences, text).sqLiteQuery
        cacheRepository.getApplications(query)
    }

    init {
        syncDatabase()
    }

    fun search(text: String?) {
        _searchQuery.value = text
    }

    fun syncDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            applicationsLoader.syncDatabase()
        }
    }
}
