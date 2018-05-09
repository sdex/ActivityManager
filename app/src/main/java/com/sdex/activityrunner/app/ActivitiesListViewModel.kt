package com.sdex.activityrunner.app

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.preference.PreferenceManager
import com.sdex.activityrunner.db.AppDatabase
import com.sdex.activityrunner.db.activity.ActivityModel
import com.sdex.activityrunner.preferences.AdvancedPreferences

class ActivitiesListViewModel(application: Application) : AndroidViewModel(application) {

  private val appDatabase: AppDatabase = AppDatabase.getDatabase(application)
  private val advancedPreferences: AdvancedPreferences

  init {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    advancedPreferences = AdvancedPreferences(sharedPreferences)
  }

  fun getItems(packageName: String): LiveData<List<ActivityModel>> {
    val showNotExported = advancedPreferences.isShowNotExported
    return appDatabase.activityModelDao.getActivityModels(packageName,
      if (showNotExported) -1 else 0)
  }

}
