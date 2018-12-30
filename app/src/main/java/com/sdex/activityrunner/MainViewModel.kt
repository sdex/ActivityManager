package com.sdex.activityrunner

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private var liveData: MutableLiveData<List<PackageInfo>> = MutableLiveData()

  val packages: MutableLiveData<List<PackageInfo>>
    get() {
      GlobalScope.launch {
        val list = loadPackages()
        liveData.postValue(list)
      }
      return liveData
    }

  private fun loadPackages(): List<PackageInfo> {
    return packageManager.getInstalledPackages(0)
  }
}
