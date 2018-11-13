package com.sdex.activityrunner.app

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SimpleSQLiteQuery
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery

class ApplicationsListViewModel(application: Application) : AndroidViewModel(application) {

  private val cacheDatabase: CacheDatabase = CacheDatabase.getDatabase(application)

  fun getItems(searchText: String?): LiveData<List<ApplicationModel>> {
    val query = GetApplicationsQuery(searchText)
    return cacheDatabase.applicationsModelDao
      .getApplicationModels(SimpleSQLiteQuery(query.toString()))
  }
}
