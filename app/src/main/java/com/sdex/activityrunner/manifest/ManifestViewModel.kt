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

    private val _manifestLiveData = MutableLiveData<ManifestState>()
    val manifestLiveData: LiveData<ManifestState> = _manifestLiveData

    private var job: Job? = null

    fun loadManifest(packageName: String) {
        job?.cancel()
        // parsing is one time operation, so check for filled live data to avoid unnecessary recreating
        if(_manifestLiveData.value != null) return
        job = viewModelScope.launch(Dispatchers.IO) {
            val manifestReader = ManifestReader()
            val manifestWriter = ManifestWriter()
            val manifest = manifestReader.loadAndroidManifest(getApplication(), packageName)
            _manifestLiveData.postValue(ManifestState(false,manifest ?: "Error during parsing AndroidManifest.xml"))
            manifestWriter.saveAndroidManifest(getApplication(), packageName, manifest)
        }
    }

    fun updatePosition(position: Int) {
        _manifestLiveData.value = _manifestLiveData.value?.copy(position = position, isDataReady = false)
    }

    fun setDataReady() {
        _manifestLiveData.value = _manifestLiveData.value?.copy(isDataReady = true)
    }

    data class ManifestState(val isDataReady: Boolean, val data: String, val position: Int = 0)

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
