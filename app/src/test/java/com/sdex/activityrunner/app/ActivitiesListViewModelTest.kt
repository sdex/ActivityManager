package com.sdex.activityrunner.app

import android.content.pm.PackageInfo
import android.content.res.Resources
import androidx.sqlite.db.SupportSQLiteQuery
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.preferences.PreferencesState
import com.sdex.activityrunner.util.ChangedPackages
import com.sdex.activityrunner.util.PackageInfoProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ActivitiesListViewModelTest {

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
    fun `empty search does not emit unchanged list`() = runTest(dispatcher) {
        val viewModel = viewModel(
            activities = listOf(
                activity(name = "AlphaActivity", className = ".AlphaActivity"),
                activity(name = "BetaActivity", className = ".BetaActivity"),
            ),
        )

        viewModel.uiState.test {
            assertThat(awaitItem().isLoading).isTrue()

            viewModel.getItems(PACKAGE_NAME, application())
            val loadedState = awaitItem()
            assertThat(loadedState.application).isNotNull()
            assertThat(loadedState.activities.map { it.name })
                .containsExactly("AlphaActivity", "BetaActivity")
                .inOrder()

            viewModel.filterItems("")

            expectNoEvents()

            viewModel.filterItems("alpha")
            val alphaState = awaitItem()

            assertThat(alphaState.activities.map { it.name }).containsExactly("AlphaActivity")

            viewModel.filterItems("alpha")

            expectNoEvents()

            viewModel.filterItems("")
            val restoredState = awaitItem()

            assertThat(restoredState.activities.map { it.name })
                .containsExactly("AlphaActivity", "BetaActivity")
                .inOrder()
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search entered before load is applied to loaded items`() = runTest(dispatcher) {
        val viewModel = viewModel(
            activities = listOf(
                activity(name = "AlphaActivity", className = ".AlphaActivity"),
                activity(name = "BetaActivity", className = ".BetaActivity"),
            ),
        )

        viewModel.uiState.test {
            assertThat(awaitItem().isLoading).isTrue()

            viewModel.filterItems("beta")
            assertThat(awaitItem().searchText).isEqualTo("beta")

            viewModel.getItems(PACKAGE_NAME, application())
            val loadedState = awaitItem()

            assertThat(loadedState.searchText).isEqualTo("beta")
            assertThat(loadedState.activities.map { it.name }).containsExactly("BetaActivity")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `reload keeps current search query`() = runTest(dispatcher) {
        val viewModel = viewModel(
            activities = listOf(
                activity(name = "AlphaActivity", className = ".AlphaActivity"),
                activity(
                    name = "HiddenActivity",
                    className = ".HiddenActivity",
                    exported = false,
                ),
            ),
        )

        viewModel.uiState.test {
            assertThat(awaitItem().isLoading).isTrue()

            viewModel.getItems(PACKAGE_NAME, application())
            assertThat(awaitItem().activities.map { it.name }).containsExactly("AlphaActivity")

            viewModel.filterItems("hidden")
            assertThat(awaitItem().activities).isEmpty()

            viewModel.reloadItems(PACKAGE_NAME, showNotExported = true)
            val reloadedState = awaitItem()

            assertThat(reloadedState.searchText).isEqualTo("hidden")
            assertThat(reloadedState.activities.map { it.name }).containsExactly("HiddenActivity")
            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun viewModel(
        activities: List<ActivityModel>,
        showNotExported: Boolean = false,
    ) = ActivitiesListViewModel(
        packageInfoProvider = FakePackageInfoProvider(activities),
        appPreferences = FakeAppPreferences(showNotExported),
        cacheRepository = FakeCacheRepository(),
        ioDispatcher = dispatcher,
    )

    private fun activity(
        name: String,
        className: String,
        label: String? = null,
        exported: Boolean = true,
    ) = ActivityModel(
        name = name,
        packageName = PACKAGE_NAME,
        className = className,
        label = label,
        exported = exported,
        enabled = true,
        permission = null,
    )

    private fun application() = ApplicationModel(
        packageName = PACKAGE_NAME,
        name = "Test App",
        activitiesCount = 2,
        exportedActivitiesCount = 2,
        system = false,
        enabled = true,
        versionCode = 1,
        versionName = "1.0",
        updateTime = 0,
        installTime = 0,
    )

    private class FakePackageInfoProvider(
        private val activities: List<ActivityModel>,
    ) : PackageInfoProvider {

        override fun getInstalledPackages(): List<String> = emptyList()
        override fun getApplication(packageName: String): ApplicationModel? = null
        override fun getActivities(packageName: String): List<ActivityModel> = activities
        override fun getPackageInfo(packageName: String): PackageInfo = error("Not used")
        override fun getResourcesForApplication(packageName: String): Resources = error("Not used")
        override fun getChangedPackages(lastSequenceNumber: Int): ChangedPackages? = null
    }

    private class FakeAppPreferences(
        override var showNotExported: Boolean,
    ) : AppPreferences {

        override val preferences: Flow<PreferencesState> = emptyFlow()
        override val displayConfig: Flow<DisplayConfig> = emptyFlow()
        override var isNotExportedDialogShown: Boolean = false
        override val appOpenCounter: Int = 0
        override var isShowSystemApps: Boolean = false
        override var isShowSystemAppIndicator: Boolean = false
        override var isShowDisabledApps: Boolean = false
        override var isShowDisabledAppIndicator: Boolean = false
        override var showLineNumbers: Boolean = false
        override var theme: Int = 0
        override var sortBy: String = ApplicationModel.NAME
        override var sortOrder: String = "ASC"
        override var suExecutable: String = ""
        override var lastSequenceNumber: Int = 0
        override var lastBootCount: Int = 0

        override fun onAppOpened() = Unit
    }

    private class FakeCacheRepository : CacheRepository {

        override fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> =
            emptyFlow()

        override suspend fun getApplications(packages: Set<String>?): List<ApplicationModel> =
            emptyList()

        override suspend fun getApplicationPackageNames(): List<String> = emptyList()
        override suspend fun upsert(models: List<ApplicationModel>) = Unit
        override suspend fun delete(models: List<ApplicationModel>): Int = 0
        override suspend fun getApplication(packageName: String): ApplicationModel? = null
        override suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int = 0
        override suspend fun count(): Int = 0
    }

    private companion object {

        const val PACKAGE_NAME = "com.test"
    }
}
