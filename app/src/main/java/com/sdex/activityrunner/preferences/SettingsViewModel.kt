package com.sdex.activityrunner.preferences

import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.util.ApplicationsLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val applicationsLoader: ApplicationsLoader,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    private val _isClearingCache = MutableStateFlow(false)
    val isClearingCache: StateFlow<Boolean> = _isClearingCache

    fun clearCache() {
        if (_isClearingCache.value) {
            return
        }

        coroutineScope.launch {
            _isClearingCache.update { true }
            try {
                applicationsLoader.rebuildCache()
            } finally {
                _isClearingCache.update { false }
            }
        }
    }
}
