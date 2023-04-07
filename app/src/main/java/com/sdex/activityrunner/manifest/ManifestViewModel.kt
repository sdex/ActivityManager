package com.sdex.activityrunner.manifest

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManifestViewModel @Inject constructor(
    private val manifestReader: ManifestReader,
    private val manifestWriter: ManifestWriter,
) : ViewModel() {

    private val _manifestLiveData = MutableLiveData<String?>()
    val manifestLiveData: LiveData<String?> = _manifestLiveData

    private var job: Job? = null

    fun loadManifest(packageName: String) {
        job?.cancel()
        if (_manifestLiveData.value != null) {
            return
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            val manifest = manifestReader.load(packageName)
            _manifestLiveData.postValue(manifest)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    fun export(uri: Uri) {
        val data = _manifestLiveData.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            manifestWriter.write(uri, data)
        }
    }
}
