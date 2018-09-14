package com.sdex.activityrunner.manifest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

  private var liveData = MutableLiveData<String>()
  private var deferred: Deferred<String?>? = null

  fun loadManifest(packageName: String): MutableLiveData<String> {
    deferred = async(CommonPool) {
      val manifestReader = ManifestReader()
      val manifestWriter = ManifestWriter()
      val manifest = manifestReader.loadAndroidManifest(getApplication(), packageName)
      liveData.postValue(manifest)
      manifestWriter.saveAndroidManifest(getApplication(), packageName, manifest)
      manifest
    }
    return liveData
  }

  override fun onCleared() {
    super.onCleared()
    deferred?.cancel()
  }
}
