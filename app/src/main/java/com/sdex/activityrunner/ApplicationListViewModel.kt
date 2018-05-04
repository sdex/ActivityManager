package com.sdex.activityrunner

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SimpleSQLiteQuery
import android.preference.PreferenceManager
import com.sdex.activityrunner.db.AppDatabase
import com.sdex.activityrunner.db.application.ApplicationModel
import com.sdex.activityrunner.db.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.SortingPreferences

class ApplicationListViewModel(application: Application) : AndroidViewModel(application) {

  private val appDatabase: AppDatabase = AppDatabase.getDatabase(application)
  private val sortingPreferences: SortingPreferences

  init {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    sortingPreferences = SortingPreferences(sharedPreferences)
  }

  fun getItems(searchText: String?): LiveData<List<ApplicationModel>> {
    val query = GetApplicationsQuery(searchText, sortingPreferences)
    return appDatabase.applicationModelDao
      .getApplicationModels(SimpleSQLiteQuery(query.toString()))
  }
}
