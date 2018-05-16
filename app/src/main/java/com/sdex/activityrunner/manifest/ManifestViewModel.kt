package com.sdex.activityrunner.manifest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.os.AsyncTask

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

  private var manifestData: MutableLiveData<String>? = null

  fun loadManifest(packageName: String): MutableLiveData<String> {
    if (manifestData == null) {
      manifestData = MutableLiveData()
      loadAndroidManifest(packageName)
    }
    return manifestData!!
  }

  private fun loadAndroidManifest(packageName: String) {
    val loadTask = LoadTask(getApplication(), packageName, manifestData!!)
    loadTask.execute()
  }

  private class LoadTask(private val context: Context,
                         private val packageName: String,
                         private val liveData: MutableLiveData<String>)
    : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg voids: Void): Void? {
      val manifestReader = ManifestReader()
      val androidManifest = manifestReader.loadAndroidManifest(context, packageName)
      liveData.postValue(androidManifest)
      return null
    }
  }
}
