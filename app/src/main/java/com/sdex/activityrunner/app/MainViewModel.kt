package com.sdex.activityrunner.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
) : ViewModel() {

    val searchQuery = MutableLiveData<String?>(null)

    val items: LiveData<List<ApplicationModel>> = Transformations.switchMap(searchQuery) { text ->
        val query = GetApplicationsQuery(text).sqLiteQuery
        cacheRepository.getApplications(query)
    }
}
