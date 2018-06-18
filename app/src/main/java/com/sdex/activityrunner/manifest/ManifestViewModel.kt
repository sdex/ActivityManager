package com.sdex.activityrunner.manifest

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

  private var liveData = MutableLiveData<String>()

  fun loadManifest(packageName: String): MutableLiveData<String> {
    val deferred = async(CommonPool) {
      val manifestReader = ManifestReader()
      manifestReader.loadAndroidManifest(getApplication(), packageName)
    }
    launch(UI) {
      liveData.value = deferred.await()
    }
    return liveData
  }
}
