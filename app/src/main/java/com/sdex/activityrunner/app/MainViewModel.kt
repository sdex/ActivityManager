package com.sdex.activityrunner.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val cacheDatabase = CacheDatabase.getDatabase(application)

    val searchQuery = MutableLiveData<String?>(null)

    val items: LiveData<List<ApplicationModel>> = Transformations.switchMap(searchQuery) { text ->
        val query = SimpleSQLiteQuery(GetApplicationsQuery(text).toString())
        cacheDatabase.applicationsModelDao.getApplicationModels(query)
    }
}
