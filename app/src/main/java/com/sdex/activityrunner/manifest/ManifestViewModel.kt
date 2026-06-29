package com.sdex.activityrunner.manifest

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.di.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface ManifestUiState {
    data object Idle : ManifestUiState
    data object Loading : ManifestUiState
    data class Loaded(val manifest: String) : ManifestUiState
    data object Failed : ManifestUiState
}

@HiltViewModel
class ManifestViewModel @Inject constructor(
    private val manifestReader: ManifestReader,
    private val manifestWriter: ManifestWriter,
    private val coroutineScope: CoroutineScope,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ManifestUiState>(ManifestUiState.Idle)
    val uiState: StateFlow<ManifestUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadManifest(packageName: String) {
        if (_uiState.value is ManifestUiState.Loaded || loadJob?.isActive == true) {
            return
        }

        loadJob = viewModelScope.launch {
            _uiState.value = ManifestUiState.Loading
            val manifest = withContext(ioDispatcher) {
                manifestReader.load(packageName)
            }
            _uiState.value = if (manifest == null) {
                ManifestUiState.Failed
            } else {
                ManifestUiState.Loaded(manifest)
            }
            loadJob = null
        }
    }

    fun export(uri: Uri) {
        val data = (_uiState.value as? ManifestUiState.Loaded)?.manifest ?: return
        coroutineScope.launch {
            manifestWriter.write(uri, data)
        }
    }
}
