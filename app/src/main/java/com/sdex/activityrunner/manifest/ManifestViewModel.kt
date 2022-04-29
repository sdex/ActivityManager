package com.sdex.activityrunner.manifest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

    private val _manifestLiveData = MutableLiveData<String?>()
    val manifestLiveData: LiveData<String?> = _manifestLiveData

    private var job: Job? = null

    fun loadManifest(packageName: String) {
        job?.cancel()
        // parsing is one time operation, so check for filled live data to avoid unnecessary recreating
        if(_manifestLiveData.value != null) return
        job = viewModelScope.launch(Dispatchers.IO) {
            val manifestReader = ManifestReader()
            val manifestWriter = ManifestWriter()
            val manifest = manifestReader.loadAndroidManifest(getApplication(), packageName)
            _manifestLiveData.postValue(manifest)
            manifestWriter.saveAndroidManifest(getApplication(), packageName, manifest)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
