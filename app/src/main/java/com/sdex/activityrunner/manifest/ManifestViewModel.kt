package com.sdex.activityrunner.manifest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

  private var liveData = MutableLiveData<String>()
  private var job: Job? = null

  fun loadManifest(packageName: String): MutableLiveData<String> {
    job = GlobalScope.launch {
      val manifestReader = ManifestReader()
      val manifestWriter = ManifestWriter()
      val manifest = manifestReader.loadAndroidManifest(getApplication(), packageName)
      liveData.postValue(manifest)
      manifestWriter.saveAndroidManifest(getApplication(), packageName, manifest)
    }
    return liveData
  }

  override fun onCleared() {
    super.onCleared()
    job?.cancel()
  }
}
