package com.sdex.activityrunner

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private var liveData: MutableLiveData<List<PackageInfo>> = MutableLiveData()

  val packages: MutableLiveData<List<PackageInfo>>
    get() {
      async(UI) {
        val list = loadPackages()
        liveData.postValue(list)
      }
      return liveData
    }

  private fun loadPackages(): List<PackageInfo> {
    return packageManager.getInstalledPackages(0)
  }
}
