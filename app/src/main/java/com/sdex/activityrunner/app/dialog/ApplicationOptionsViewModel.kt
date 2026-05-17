package com.sdex.activityrunner.app.dialog

import androidx.lifecycle.ViewModel
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationOptionsViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    fun togglePinned(model: ApplicationModel) {
        val pinnedAt = if (model.pinnedAt == 0L) {
            System.currentTimeMillis()
        } else {
            0L
        }
        coroutineScope.launch {
            cacheRepository.updatePinnedAt(model.packageName, pinnedAt)
        }
    }
}
