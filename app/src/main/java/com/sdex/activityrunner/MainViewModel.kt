package com.sdex.activityrunner

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask

class MainViewModel(application: Application) : AndroidViewModel(application) {

  private val packageManager: PackageManager = application.packageManager
  private var liveData: MutableLiveData<List<PackageInfo>>? = null

  val packages: MutableLiveData<List<PackageInfo>>
    get() {
      if (liveData == null) {
        liveData = MutableLiveData()
        loadPackages(liveData!!)
      }
      return liveData!!
    }

  private fun loadPackages(liveData: MutableLiveData<List<PackageInfo>>) {
    val loader = Loader(packageManager, liveData)
    loader.execute()
  }

  private class Loader(private val packageManager: PackageManager,
                       private val liveData: MutableLiveData<List<PackageInfo>>) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg voids: Void): Void? {
      val installedPackages = packageManager.getInstalledPackages(0)
      liveData.postValue(installedPackages)
      return null
    }
  }
}
