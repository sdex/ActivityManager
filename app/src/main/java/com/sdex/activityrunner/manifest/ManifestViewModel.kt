package com.sdex.activityrunner.manifest

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.FileWriter

class ManifestViewModel(application: Application) : AndroidViewModel(application) {

    private val _manifestLiveData = MutableLiveData<String?>()
    val manifestLiveData: LiveData<String?> = _manifestLiveData

    private var job: Job? = null

    fun loadManifest(packageName: String) {
        job?.cancel()
        if (_manifestLiveData.value != null) {
            return
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            val manifestReader = ManifestReader()
            val context = getApplication<Application>()
            val manifest = manifestReader.loadAndroidManifest(context, packageName)
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
            val context = getApplication<Application>()
            context.contentResolver.openFileDescriptor(uri, "w")?.use {
                val fileWriter = FileWriter(it.fileDescriptor)
                fileWriter.use { fileWriter.write(data) }
            }
        }
    }
}
