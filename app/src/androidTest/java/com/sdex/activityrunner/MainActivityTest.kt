package com.sdex.activityrunner

import android.content.pm.PackageInfo
import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.sdex.activityrunner.app.ApplicationsListAdapter
import com.sdex.activityrunner.commons.platform.EnvironmentInfoProvider
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.db.history.HistoryModel
import com.sdex.activityrunner.db.history.HistoryModelDao
import com.sdex.activityrunner.di.AppModule
import com.sdex.activityrunner.di.DatabaseModule
import com.sdex.activityrunner.di.IoDispatcher
import com.sdex.activityrunner.manifest.DefaultManifestWriter
import com.sdex.activityrunner.manifest.ManifestReader
import com.sdex.activityrunner.manifest.ManifestWriter
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.preferences.PreferencesState
import com.sdex.activityrunner.util.ApplicationsLoader
import com.sdex.activityrunner.util.ChangedPackages
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import org.hamcrest.Matchers.allOf
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@HiltAndroidTest
@UninstallModules(AppModule::class, DatabaseModule::class)
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val fakeCacheRepository = FakeCacheRepository(
        applications = listOf(
            application(name = "Alpha Browser", packageName = "com.test.alpha"),
            application(name = "Beta Camera", packageName = "com.test.beta"),
        ),
    )
    private val fakeAppPreferences = FakeAppPreferences()
    private val fakePackageInfoProvider = FakePackageInfoProvider(
        packageNames = fakeCacheRepository.applicationPackageNames,
    )
    private val fakeEnvironmentInfoProvider = FakeEnvironmentInfoProvider()

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val cacheRepository: CacheRepository = fakeCacheRepository

    @BindValue
    @JvmField
    val appPreferences: AppPreferences = fakeAppPreferences

    @BindValue
    @JvmField
    val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @BindValue
    @JvmField
    val historyModelDao: HistoryModelDao = FakeHistoryModelDao()

    @BindValue
    @JvmField
    val manifestWriter: ManifestWriter = DefaultManifestWriter(
        InstrumentationRegistry.getInstrumentation().targetContext,
    )

    @BindValue
    @JvmField
    val manifestReader: ManifestReader = FakeManifestReader()

    @BindValue
    @IoDispatcher
    @JvmField
    val ioDispatcher: CoroutineDispatcher = Dispatchers.Main.immediate

    @BindValue
    @JvmField
    val applicationsLoader: ApplicationsLoader = ApplicationsLoader(
        cacheRepository = fakeCacheRepository,
        packageInfoProvider = fakePackageInfoProvider,
        preferences = fakeAppPreferences,
        environmentInfoProvider = fakeEnvironmentInfoProvider,
    )

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun searchFiltersMainAppListUsingInjectedRepository() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withText("Alpha Browser")).check(matches(isDisplayed()))
            onView(withText("Beta Camera")).check(matches(isDisplayed()))

            onView(withId(R.id.action_search)).perform(click())
            onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText("beta"))
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.list))
                .perform(RecyclerViewActions.scrollTo<ApplicationsListAdapter.AppViewHolder>(
                    hasDescendant(withText("Beta Camera")),
                ))
            onView(withText("Beta Camera")).check(matches(isDisplayed()))
            onView(withId(R.id.list)).check(matches(hasItemCount(1)))
        }

        assertContainsInOrder(
            fakeCacheRepository.applicationQueries,
            listOf(
                "SELECT * FROM ApplicationModel WHERE activitiesCount>0 ORDER BY pinnedAt DESC, name COLLATE NOCASE ASC",
                "SELECT * FROM ApplicationModel WHERE activitiesCount>0 AND (name LIKE '%beta%' OR packageName LIKE '%beta%') ORDER BY pinnedAt DESC, name COLLATE NOCASE ASC",
            ),
        )
    }

    @Test
    fun displayConfigControlsMainListFilteringThroughHiltGraph() {
        fakeAppPreferences.displayConfigState.value = DisplayConfig(showDisabledApps = false)

        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withText("Alpha Browser")).check(matches(isDisplayed()))
            onView(withText("Beta Camera")).check(doesNotExist())
            onView(withId(R.id.list))
                .check(matches(allOf(
                    hasDescendant(withText("Alpha Browser")),
                )))
        }

        assertTrue(fakeCacheRepository.applicationQueries.last().contains("AND (enabled=1)"))
    }

    private fun application(
        name: String,
        packageName: String,
        enabled: Boolean = packageName != "com.test.beta",
    ) = ApplicationModel(
        packageName = packageName,
        name = name,
        activitiesCount = 1,
        exportedActivitiesCount = 1,
        system = false,
        enabled = enabled,
        versionCode = 1,
        versionName = "1.0",
        updateTime = 0,
        installTime = 0,
    )

    private fun assertContainsInOrder(
        actual: List<String>,
        expected: List<String>,
    ) {
        var nextExpectedIndex = 0
        actual.forEach { actualItem ->
            if (actualItem == expected[nextExpectedIndex]) {
                nextExpectedIndex++
                if (nextExpectedIndex == expected.size) {
                    return
                }
            }
        }
        throw AssertionError("Expected $actual to contain $expected in order")
    }

    private fun hasItemCount(expectedCount: Int) = object : org.hamcrest.TypeSafeMatcher<android.view.View>() {

        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("RecyclerView with $expectedCount items")
        }

        override fun matchesSafely(item: android.view.View): Boolean {
            return (item as? RecyclerView)?.adapter?.itemCount == expectedCount
        }
    }

    private class FakeCacheRepository(
        private val applications: List<ApplicationModel>,
    ) : CacheRepository {

        val applicationQueries = mutableListOf<String>()
        val applicationPackageNames = applications.map { it.packageName }

        override fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> {
            val sql = query.sql
            applicationQueries += sql
            return flowOf(applications.filter { it.matches(sql) }.sortedWith(sqlComparator(sql)))
        }

        override suspend fun getApplications(packages: Set<String>?): List<ApplicationModel> {
            return applications.filter { packages == null || it.packageName in packages }
        }

        override suspend fun getApplicationPackageNames(): List<String> = applicationPackageNames
        override suspend fun upsert(models: List<ApplicationModel>) = Unit
        override suspend fun delete(models: List<ApplicationModel>): Int = 0
        override suspend fun getApplication(packageName: String): ApplicationModel? =
            applications.firstOrNull { it.packageName == packageName }

        override suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int = 0
        override suspend fun count(): Int = applications.size

        private fun ApplicationModel.matches(sql: String): Boolean {
            if (sql.contains("(enabled=1)") && !enabled) {
                return false
            }
            if (sql.contains("(system=0)") && system) {
                return false
            }

            val searchText = searchTextFrom(sql) ?: return true
            return name?.contains(searchText, ignoreCase = true) == true ||
                packageName.contains(searchText, ignoreCase = true)
        }

        private fun searchTextFrom(sql: String): String? {
            val match = Regex("LIKE '%((?:''|[^'])*)%'").find(sql) ?: return null
            return match.groupValues[1].replace("''", "'")
        }

        private fun sqlComparator(sql: String): Comparator<ApplicationModel> {
            val comparator = compareBy<ApplicationModel> { it.name?.lowercase().orEmpty() }
            return if (sql.endsWith(" DESC")) {
                comparator.reversed()
            } else {
                comparator
            }
        }
    }

    private class FakeAppPreferences : AppPreferences {

        val displayConfigState = MutableStateFlow(DisplayConfig())

        override val preferences: Flow<PreferencesState> = emptyFlow()
        override val displayConfig: Flow<DisplayConfig> = displayConfigState
        override var isNotExportedDialogShown: Boolean = true
        override val appOpenCounter: Int = 0
        override var isShowSystemApps: Boolean = true
        override var isShowSystemAppIndicator: Boolean = false
        override var isShowDisabledApps: Boolean = true
        override var isShowDisabledAppIndicator: Boolean = false
        override var showNotExported: Boolean = false
        override var showLineNumbers: Boolean = false
        override var theme: Int = 0
        override var sortBy: String = ApplicationModel.NAME
        override var sortOrder: String = "ASC"
        override var suExecutable: String = ""
        override var lastSequenceNumber: Int = 1
        override var lastBootCount: Int = 1

        override fun onAppOpened() = Unit
    }

    private class FakePackageInfoProvider(
        private val packageNames: List<String>,
    ) : PackageInfoProvider {

        override fun getInstalledPackages(): List<String> = packageNames
        override fun getApplication(packageName: String): ApplicationModel? = null
        override fun getActivities(packageName: String) = emptyList<com.sdex.activityrunner.app.ActivityModel>()
        override fun getPackageInfo(packageName: String): PackageInfo = error("Not used")
        override fun getResourcesForApplication(packageName: String): Resources = error("Not used")
        override fun getChangedPackages(lastSequenceNumber: Int): ChangedPackages? = null
    }

    private class FakeManifestReader : ManifestReader {

        override fun load(packageName: String): String = "<manifest package=\"$packageName\" />"
    }

    private class FakeEnvironmentInfoProvider : EnvironmentInfoProvider {

        override val isQuickSyncSupported: Boolean = true
        override val bootCount: Int = 1
    }

    private class FakeHistoryModelDao : HistoryModelDao {

        private val history = MutableStateFlow(emptyList<HistoryModel>())

        override suspend fun insert(vararg model: HistoryModel) = Unit
        override suspend fun update(vararg models: HistoryModel) = Unit
        override suspend fun delete(vararg models: HistoryModel) = Unit
        override fun getHistory(): Flow<List<HistoryModel>> = history
        override suspend fun clean() = Unit
    }
}
