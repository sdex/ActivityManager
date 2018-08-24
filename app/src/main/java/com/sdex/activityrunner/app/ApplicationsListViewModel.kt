package com.sdex.activityrunner.app

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SimpleSQLiteQuery
import android.preference.PreferenceManager
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.SortingPreferences

class ApplicationsListViewModel(application: Application) : AndroidViewModel(application) {

  private val cacheDatabase: CacheDatabase = CacheDatabase.getDatabase(application)
  private val sortingPreferences: SortingPreferences =
    SortingPreferences(PreferenceManager.getDefaultSharedPreferences(application))

  fun getItems(searchText: String?): LiveData<List<ApplicationModel>> {
    val query = GetApplicationsQuery(searchText, sortingPreferences)
    return cacheDatabase.applicationsModelDao
      .getApplicationModels(SimpleSQLiteQuery(query.toString()))
  }
}
