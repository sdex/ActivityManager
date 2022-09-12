package com.sdex.activityrunner.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _searchQuery = MutableLiveData<String?>(null)
    val searchQuery: LiveData<String?> = _searchQuery

    val items: LiveData<List<ApplicationModel>> = Transformations.switchMap(searchQuery) { text ->
        val query = GetApplicationsQuery(appPreferences, text).sqLiteQuery
        cacheRepository.getApplications(query)
    }

    fun search(text: String?) {
        _searchQuery.value = text
    }
}
