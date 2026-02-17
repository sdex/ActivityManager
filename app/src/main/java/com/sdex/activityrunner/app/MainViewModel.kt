package com.sdex.activityrunner.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.util.ApplicationsLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class UiState(
    val items: List<ApplicationModel>,
    val displayConfig: DisplayConfig,
    val isSyncing: Boolean = false,
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheRepository: CacheRepository,
    appPreferences: AppPreferences,
    private val applicationsLoader: ApplicationsLoader,
    private val coroutineScope: CoroutineScope,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery: StateFlow<String?> = _searchQuery

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<UiState> = combine(
        searchQuery,
        appPreferences.displayConfig,
    ) { text, displayConfig -> text to displayConfig }
        .flatMapLatest { (text, displayConfig) ->
            val query = GetApplicationsQuery(displayConfig, text)
            Timber.d("Query: $query")
            cacheRepository.getApplications(query.sqLiteQuery)
                .map { items ->
                    UiState(
                        items = items,
                        displayConfig = displayConfig,
                    )
                }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            UiState(
                items = emptyList(),
                displayConfig = DisplayConfig(),
            ),
        )

    init {
        sync()
    }

    fun search(text: String?) {
        Timber.d("Search: $text")
        _searchQuery.value = text
    }

    private fun sync() {
        coroutineScope.launch {
            val shouldSync = applicationsLoader.shouldSync()
            if (shouldSync) {
                if (cacheRepository.count() > 0) {
                    // set isSyncing to true only if there are applications in the database
                    // the app shouldn't show this progress during the initial sync
                    _isSyncing.update { true }
                }

                applicationsLoader.sync()

                _isSyncing.update { false }
            }
        }
    }

    fun quickSync() {
        if (applicationsLoader.isQuickSyncSupported) {
            sync()
        }
    }
}
