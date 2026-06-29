package com.sdex.activityrunner.app

import androidx.sqlite.db.SupportSQLiteQuery
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.cache.query.GetApplicationsQuery
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.preferences.PreferencesState
import com.sdex.activityrunner.util.ApplicationsLoader
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState emits applications for display config and search query`() = runTest(dispatcher) {
        val defaultConfig = DisplayConfig()
        val appPreferences = FakeAppPreferences(displayConfig = defaultConfig)
        val repository = FakeCacheRepository(
            applicationsByQuery = mapOf(
                GetApplicationsQuery(defaultConfig).toString() to listOf(app("Alpha")),
                GetApplicationsQuery(defaultConfig, "beta").toString() to listOf(app("Beta")),
            ),
        )
        val viewModel = viewModel(
            cacheRepository = repository,
            appPreferences = appPreferences,
        )

        viewModel.uiState.test {
            assertThat(awaitItem().items).isEmpty()
            runCurrent()
            assertThat(awaitItem().items.map { it.name }).containsExactly("Alpha")

            viewModel.search("beta")
            assertThat(viewModel.searchQuery.value).isEqualTo("beta")
            runCurrent()

            assertThat(awaitItem().items.map { it.name }).containsExactly("Beta")
            assertThat(repository.applicationQueries)
                .containsExactly(
                    GetApplicationsQuery(defaultConfig).toString(),
                    GetApplicationsQuery(defaultConfig, "beta").toString(),
                )
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState requeries applications when display config changes`() = runTest(dispatcher) {
        val defaultConfig = DisplayConfig()
        val enabledOnlyConfig = defaultConfig.copy(showDisabledApps = false)
        val appPreferences = FakeAppPreferences(displayConfig = defaultConfig)
        val repository = FakeCacheRepository(
            applicationsByQuery = mapOf(
                GetApplicationsQuery(defaultConfig).toString() to listOf(app("Default")),
                GetApplicationsQuery(enabledOnlyConfig).toString() to listOf(app("Enabled")),
            ),
        )
        val viewModel = viewModel(
            cacheRepository = repository,
            appPreferences = appPreferences,
        )

        viewModel.uiState.test {
            skipItems(1)
            runCurrent()
            assertThat(awaitItem().items.map { it.name }).containsExactly("Default")

            appPreferences.displayConfigState.value = enabledOnlyConfig
            runCurrent()

            val state = awaitItem()
            assertThat(state.displayConfig).isEqualTo(enabledOnlyConfig)
            assertThat(state.items.map { it.name }).containsExactly("Enabled")
            assertThat(repository.applicationQueries)
                .containsExactly(
                    GetApplicationsQuery(defaultConfig).toString(),
                    GetApplicationsQuery(enabledOnlyConfig).toString(),
                )
                .inOrder()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `init syncs when needed without showing syncing for empty cache`() = runTest(dispatcher) {
        val repository = FakeCacheRepository(count = 0)
        val applicationsLoader = applicationsLoader(shouldSync = true)
        val viewModel = viewModel(
            cacheRepository = repository,
            applicationsLoader = applicationsLoader,
        )

        advanceUntilIdle()

        assertThat(viewModel.isSyncing.value).isFalse()
        coVerify(exactly = 1) { applicationsLoader.shouldSync() }
        coVerify(exactly = 1) { applicationsLoader.sync() }
    }

    @Test
    fun `sync exposes syncing while refreshing existing cache`() = runTest(dispatcher) {
        val syncStarted = CompletableDeferred<Unit>()
        val syncRelease = CompletableDeferred<Unit>()
        val repository = FakeCacheRepository(count = 1)
        val applicationsLoader = applicationsLoader(
            shouldSync = true,
            syncAction = {
                syncStarted.complete(Unit)
                syncRelease.await()
            },
        )
        val viewModel = viewModel(
            cacheRepository = repository,
            applicationsLoader = applicationsLoader,
        )

        runCurrent()
        syncStarted.await()

        assertThat(viewModel.isSyncing.value).isTrue()

        syncRelease.complete(Unit)
        advanceUntilIdle()

        assertThat(viewModel.isSyncing.value).isFalse()
    }

    @Test
    fun `quickSync only syncs when quick sync is supported`() = runTest(dispatcher) {
        val unsupportedLoader = applicationsLoader(isQuickSyncSupported = false)
        val unsupportedViewModel = viewModel(applicationsLoader = unsupportedLoader)
        advanceUntilIdle()

        unsupportedViewModel.quickSync()
        advanceUntilIdle()

        coVerify(exactly = 1) { unsupportedLoader.shouldSync() }

        val supportedLoader = applicationsLoader(isQuickSyncSupported = true)
        val supportedViewModel = viewModel(applicationsLoader = supportedLoader)
        advanceUntilIdle()

        supportedViewModel.quickSync()
        advanceUntilIdle()

        coVerify(exactly = 2) { supportedLoader.shouldSync() }
    }

    private fun viewModel(
        cacheRepository: FakeCacheRepository = FakeCacheRepository(),
        appPreferences: FakeAppPreferences = FakeAppPreferences(),
        applicationsLoader: ApplicationsLoader = applicationsLoader(),
        coroutineScope: CoroutineScope = TestScope(dispatcher),
    ) = MainViewModel(
        cacheRepository = cacheRepository,
        appPreferences = appPreferences,
        applicationsLoader = applicationsLoader,
        coroutineScope = coroutineScope,
    )

    private fun applicationsLoader(
        isQuickSyncSupported: Boolean = true,
        shouldSync: Boolean = false,
        syncAction: suspend () -> Unit = {},
    ) = mockk<ApplicationsLoader> {
        every { this@mockk.isQuickSyncSupported } returns isQuickSyncSupported
        coEvery { this@mockk.shouldSync() } returns shouldSync
        coEvery { this@mockk.sync() } coAnswers { syncAction() }
    }

    private fun app(name: String, packageName: String = "com.test.$name") = ApplicationModel(
        packageName = packageName,
        name = name,
        activitiesCount = 1,
        exportedActivitiesCount = 1,
        system = false,
        enabled = true,
        versionCode = 1,
        versionName = "1.0",
        updateTime = 0,
        installTime = 0,
    )

    private class FakeAppPreferences(
        displayConfig: DisplayConfig = DisplayConfig(),
    ) : AppPreferences {

        val displayConfigState = MutableStateFlow(displayConfig)

        override val preferences: Flow<PreferencesState> = emptyFlow()
        override val displayConfig: Flow<DisplayConfig> = displayConfigState
        override var isNotExportedDialogShown: Boolean = false
        override val appOpenCounter: Int = 0
        override var isShowSystemApps: Boolean = false
        override var isShowSystemAppIndicator: Boolean = false
        override var isShowDisabledApps: Boolean = false
        override var isShowDisabledAppIndicator: Boolean = false
        override var showNotExported: Boolean = false
        override var showLineNumbers: Boolean = false
        override var theme: Int = 0
        override var sortBy: String = ApplicationModel.NAME
        override var sortOrder: String = GetApplicationsQuery.ASC
        override var suExecutable: String = ""
        override var lastSequenceNumber: Int = 0
        override var lastBootCount: Int = 0

        override fun onAppOpened() = Unit
    }

    private class FakeCacheRepository(
        private val count: Int = 0,
        private val applicationsByQuery: Map<String, List<ApplicationModel>> = emptyMap(),
    ) : CacheRepository {

        val applicationQueries = mutableListOf<String>()

        override fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> {
            val sql = query.sql
            applicationQueries += sql
            return flowOf(applicationsByQuery[sql].orEmpty())
        }

        override suspend fun getApplications(packages: Set<String>?): List<ApplicationModel> =
            emptyList()

        override suspend fun getApplicationPackageNames(): List<String> = emptyList()
        override suspend fun upsert(models: List<ApplicationModel>) = Unit
        override suspend fun delete(models: List<ApplicationModel>): Int = 0
        override suspend fun getApplication(packageName: String): ApplicationModel? = null
        override suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int = 0
        override suspend fun count(): Int = count
    }
}
