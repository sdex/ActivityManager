package com.sdex.activityrunner.util

import android.content.pm.PackageInfo
import android.content.res.Resources
import androidx.sqlite.db.SupportSQLiteQuery
import com.google.common.truth.Truth.assertThat
import com.sdex.activityrunner.app.ActivityModel
import com.sdex.activityrunner.commons.platform.EnvironmentInfoProvider
import com.sdex.activityrunner.db.cache.ApplicationModel
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.DisplayConfig
import com.sdex.activityrunner.preferences.PreferencesState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ApplicationsLoaderTest {

    @Test
    fun `isQuickSyncSupported exposes environment support flag`() {
        val quickSyncLoader = loader(
            environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = true),
        )
        val fullSyncLoader = loader(
            environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = false),
        )

        assertThat(quickSyncLoader.isQuickSyncSupported).isTrue()
        assertThat(fullSyncLoader.isQuickSyncSupported).isFalse()
    }

    @Test
    fun `shouldSync returns true when quick sync is not supported`() = runTest {
        val repository = FakeCacheRepository()
        val provider = FakePackageInfoProvider()
        val loader = loader(
            cacheRepository = repository,
            packageInfoProvider = provider,
            environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = false),
        )

        assertThat(loader.shouldSync()).isTrue()
        assertThat(provider.installedPackagesRequests).isEmpty()
        assertThat(repository.getApplicationPackageNamesCalls).isEqualTo(0)
    }

    @Test
    fun `shouldSync returns true for initial quick sync`() = runTest {
        val preferences = FakeAppPreferences(lastSequenceNumber = -1)
        val provider = FakePackageInfoProvider()

        val result = loader(
            preferences = preferences,
            packageInfoProvider = provider,
        ).shouldSync()

        assertThat(result).isTrue()
        assertThat(provider.changedPackagesRequests).isEmpty()
    }

    @Test
    fun `shouldSync returns true when boot count changed`() = runTest {
        val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 1)
        val environment = FakeEnvironmentInfoProvider(bootCount = 2)
        val provider = FakePackageInfoProvider()

        val result = loader(
            preferences = preferences,
            environment = environment,
            packageInfoProvider = provider,
        ).shouldSync()

        assertThat(result).isTrue()
        assertThat(provider.changedPackagesRequests).isEmpty()
    }

    @Test
    fun `shouldSync returns true when package sequence changed`() = runTest {
        val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 3)
        val environment = FakeEnvironmentInfoProvider(bootCount = 3)
        val provider = FakePackageInfoProvider(
            changedPackages = mapOf(10 to ChangedPackages(11, listOf("com.test.changed"))),
        )

        val result = loader(
            preferences = preferences,
            environment = environment,
            packageInfoProvider = provider,
        ).shouldSync()

        assertThat(result).isTrue()
        assertThat(provider.changedPackagesRequests).isEqualTo(listOf(10))
    }

    @Test
    fun `shouldSync returns true when cache contains stale applications`() = runTest {
        val repository =
            FakeCacheRepository(packageNames = listOf("com.test.installed", "com.test.stale"))
        val provider = FakePackageInfoProvider(
            installedPackages = listOf("com.test.installed"),
            changedPackages = mapOf(10 to null),
        )

        val result = loader(
            cacheRepository = repository,
            packageInfoProvider = provider,
        ).shouldSync()

        assertThat(result).isTrue()
        assertThat(provider.changedPackagesRequests).isEqualTo(listOf(10))
        assertThat(provider.installedPackagesRequests).isEqualTo(listOf("getInstalledPackages"))
        assertThat(repository.getApplicationPackageNamesCalls).isEqualTo(1)
    }

    @Test
    fun `shouldSync returns false when quick sync has no changes and no stale cache`() = runTest {
        val repository = FakeCacheRepository(packageNames = listOf("com.test.installed"))
        val provider = FakePackageInfoProvider(
            installedPackages = listOf("com.test.installed"),
            changedPackages = mapOf(10 to null),
        )

        val result = loader(
            cacheRepository = repository,
            packageInfoProvider = provider,
        ).shouldSync()

        assertThat(result).isFalse()
        assertThat(provider.changedPackagesRequests).isEqualTo(listOf(10))
        assertThat(repository.getApplicationPackageNamesCalls).isEqualTo(1)
    }

    @Test
    fun `sync performs full sync when quick sync is not supported and does not update sync state`() =
        runTest {
            val existing = app("com.test.old")
            val newApp = app("com.test.new")
            val repository = FakeCacheRepository(applications = listOf(existing))
            val provider = FakePackageInfoProvider(
                installedPackages = listOf("com.test.new", "com.test.missing"),
                applications = mapOf("com.test.new" to newApp, "com.test.missing" to null),
            )
            val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 5)

            loader(
                cacheRepository = repository,
                packageInfoProvider = provider,
                preferences = preferences,
                environment = FakeEnvironmentInfoProvider(
                    isQuickSyncSupported = false,
                    bootCount = 6,
                ),
            ).sync()

            assertThat(repository.getApplicationsPackageRequests).isEqualTo(listOf(null))
            assertThat(repository.deletedModels.single()).isEqualTo(listOf(existing))
            assertThat(repository.upsertedModels.single()).isEqualTo(listOf(newApp))
            assertThat(preferences.lastSequenceNumber).isEqualTo(10)
            assertThat(preferences.lastBootCount).isEqualTo(5)
            assertThat(provider.changedPackagesRequests).isEmpty()
        }

    @Test
    fun `sync inserts and updates only changed applications while preserving pinned state`() =
        runTest {
            val unchanged = app("com.test.same")
            val oldChanged = app("com.test.changed", versionCode = 1, pinnedAt = 123)
            val newChanged = app("com.test.changed", versionCode = 2)
            val newInserted = app("com.test.inserted")
            val repository = FakeCacheRepository(applications = listOf(unchanged, oldChanged))
            val provider = FakePackageInfoProvider(
                installedPackages = listOf(
                    "com.test.same",
                    "com.test.changed",
                    "com.test.inserted",
                ),
                applications = mapOf(
                    unchanged.packageName to unchanged,
                    newChanged.packageName to newChanged,
                    newInserted.packageName to newInserted,
                ),
            )

            loader(
                cacheRepository = repository,
                packageInfoProvider = provider,
                environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = false),
            ).sync()

            assertThat(repository.deletedModels).isEmpty()
            assertThat(repository.upsertedModels.single())
                .isEqualTo(listOf(newChanged.copy(pinnedAt = 123), newInserted))
        }

    @Test
    fun `sync deletes cached applications missing from installed packages`() = runTest {
        val existing = app("com.test.existing")
        val stale = app("com.test.stale")
        val repository = FakeCacheRepository(applications = listOf(existing, stale))
        val provider = FakePackageInfoProvider(
            installedPackages = listOf("com.test.existing"),
            applications = mapOf(existing.packageName to existing),
        )

        loader(
            cacheRepository = repository,
            packageInfoProvider = provider,
            environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = false),
        ).sync()

        assertThat(repository.deletedModels.single()).isEqualTo(listOf(stale))
        assertThat(repository.upsertedModels).isEmpty()
    }

    @Test
    fun `sync skips repository writes when cache already matches installed applications`() =
        runTest {
            val existing = app("com.test.existing")
            val repository = FakeCacheRepository(applications = listOf(existing))
            val provider = FakePackageInfoProvider(
                installedPackages = listOf("com.test.existing"),
                applications = mapOf(existing.packageName to existing),
            )

            loader(
                cacheRepository = repository,
                packageInfoProvider = provider,
                environment = FakeEnvironmentInfoProvider(isQuickSyncSupported = false),
            ).sync()

            assertThat(repository.deletedModels).isEmpty()
            assertThat(repository.upsertedModels).isEmpty()
        }

    @Test
    fun `sync filters to changed packages during quick sync and updates sequence number`() =
        runTest {
            val changedOld = app("com.test.changed", versionCode = 1)
            val changedNew = app("com.test.changed", versionCode = 2)
            val staleInTarget = app("com.test.removed")
            val untouched = app("com.test.untouched")
            val repository = FakeCacheRepository(applications = listOf(changedOld, staleInTarget))
            val provider = FakePackageInfoProvider(
                installedPackages = listOf("com.test.changed", "com.test.untouched"),
                applications = mapOf(
                    changedNew.packageName to changedNew,
                    untouched.packageName to untouched,
                ),
                changedPackages = mapOf(
                    10 to ChangedPackages(
                        12,
                        listOf("com.test.changed", "com.test.removed"),
                    ),
                ),
            )
            val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 3)

            loader(
                cacheRepository = repository,
                packageInfoProvider = provider,
                preferences = preferences,
                environment = FakeEnvironmentInfoProvider(bootCount = 3),
            ).sync()

            assertThat(repository.getApplicationsPackageRequests.single())
                .isEqualTo(setOf("com.test.changed", "com.test.removed"))
            assertThat(repository.deletedModels.single()).isEqualTo(listOf(staleInTarget))
            assertThat(repository.upsertedModels.single()).isEqualTo(listOf(changedNew))
            assertThat(provider.changedPackagesRequests).isEqualTo(listOf(10))
            assertThat(preferences.lastSequenceNumber).isEqualTo(12)
            assertThat(preferences.lastBootCount).isEqualTo(3)
            assertThat(provider.applicationRequests).isEqualTo(listOf("com.test.changed"))
        }

    @Test
    fun `sync falls back to full package list when quick sync package details are unavailable`() =
        runTest {
            val installed = app("com.test.installed")
            val repository = FakeCacheRepository()
            val provider = FakePackageInfoProvider(
                installedPackages = listOf("com.test.installed"),
                applications = mapOf(installed.packageName to installed),
                changedPackages = mapOf(10 to null),
            )

            loader(
                cacheRepository = repository,
                packageInfoProvider = provider,
            ).sync()

            assertThat(repository.getApplicationsPackageRequests.single()).isNull()
            assertThat(repository.upsertedModels.single()).isEqualTo(listOf(installed))
        }

    @Test
    fun `sync updates boot count and sequence number after initial quick sync`() = runTest {
        val preferences = FakeAppPreferences(lastSequenceNumber = -1, lastBootCount = -1)
        val provider = FakePackageInfoProvider(
            changedPackages = mapOf(0 to ChangedPackages(7, emptyList())),
        )

        loader(
            preferences = preferences,
            packageInfoProvider = provider,
        ).sync()

        assertThat(preferences.lastSequenceNumber).isEqualTo(7)
        assertThat(preferences.lastBootCount).isEqualTo(3)
        assertThat(provider.changedPackagesRequests).isEqualTo(listOf(0))
    }

    @Test
    fun `sync stores zero sequence number when initial quick sync cannot read package changes`() =
        runTest {
            val preferences = FakeAppPreferences(lastSequenceNumber = -1, lastBootCount = -1)
            val provider = FakePackageInfoProvider(changedPackages = mapOf(0 to null))

            loader(
                preferences = preferences,
                packageInfoProvider = provider,
            ).sync()

            assertThat(preferences.lastSequenceNumber).isEqualTo(0)
            assertThat(preferences.lastBootCount).isEqualTo(3)
        }

    @Test
    fun `sync updates boot count and sequence number after reboot`() = runTest {
        val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 2)
        val installed = app("com.test.installed")
        val repository = FakeCacheRepository()
        val provider = FakePackageInfoProvider(
            installedPackages = listOf("com.test.installed"),
            applications = mapOf(installed.packageName to installed),
            changedPackages = mapOf(0 to ChangedPackages(1, emptyList())),
        )

        loader(
            cacheRepository = repository,
            preferences = preferences,
            packageInfoProvider = provider,
        ).sync()

        assertThat(preferences.lastSequenceNumber).isEqualTo(1)
        assertThat(preferences.lastBootCount).isEqualTo(3)
        assertThat(provider.changedPackagesRequests).isEqualTo(listOf(0))
        assertThat(repository.getApplicationsPackageRequests.single()).isNull()
        assertThat(repository.upsertedModels.single()).isEqualTo(listOf(installed))
    }

    @Test
    fun `sync leaves sequence number unchanged when quick sync has no changed packages`() =
        runTest {
            val preferences = FakeAppPreferences(lastSequenceNumber = 10, lastBootCount = 3)
            val provider = FakePackageInfoProvider(changedPackages = mapOf(10 to null))

            loader(
                preferences = preferences,
                packageInfoProvider = provider,
            ).sync()

            assertThat(preferences.lastSequenceNumber).isEqualTo(10)
            assertThat(preferences.lastBootCount).isEqualTo(3)
            assertThat(provider.changedPackagesRequests).isEqualTo(listOf(10))
        }

    private fun loader(
        cacheRepository: FakeCacheRepository = FakeCacheRepository(),
        packageInfoProvider: FakePackageInfoProvider = FakePackageInfoProvider(),
        preferences: FakeAppPreferences = FakeAppPreferences(
            lastSequenceNumber = 10,
            lastBootCount = 3,
        ),
        environment: FakeEnvironmentInfoProvider = FakeEnvironmentInfoProvider(bootCount = 3),
    ) = ApplicationsLoader(
        cacheRepository = cacheRepository,
        packageInfoProvider = packageInfoProvider,
        preferences = preferences,
        environmentInfoProvider = environment,
    )

    private fun app(
        packageName: String,
        name: String? = packageName,
        activitiesCount: Int = 1,
        exportedActivitiesCount: Int = 1,
        system: Boolean = false,
        enabled: Boolean = true,
        versionCode: Long = 1,
        versionName: String = "1.0",
        updateTime: Long = 100,
        installTime: Long = 50,
        installerPackage: String? = null,
        pinnedAt: Long = 0,
    ) = ApplicationModel(
        packageName = packageName,
        name = name,
        activitiesCount = activitiesCount,
        exportedActivitiesCount = exportedActivitiesCount,
        system = system,
        enabled = enabled,
        versionCode = versionCode,
        versionName = versionName,
        updateTime = updateTime,
        installTime = installTime,
        installerPackage = installerPackage,
        pinnedAt = pinnedAt,
    )

    private class FakeCacheRepository(
        private val applications: List<ApplicationModel> = emptyList(),
        private val packageNames: List<String> = applications.map { it.packageName },
    ) : CacheRepository {
        val getApplicationsPackageRequests = mutableListOf<Set<String>?>()
        val upsertedModels = mutableListOf<List<ApplicationModel>>()
        val deletedModels = mutableListOf<List<ApplicationModel>>()
        var getApplicationPackageNamesCalls = 0

        override fun getApplications(query: SupportSQLiteQuery): Flow<List<ApplicationModel>> =
            emptyFlow()

        override suspend fun getApplications(packages: Set<String>?): List<ApplicationModel> {
            getApplicationsPackageRequests += packages
            return if (packages == null) {
                applications
            } else {
                applications.filter { it.packageName in packages }
            }
        }

        override suspend fun getApplicationPackageNames(): List<String> {
            getApplicationPackageNamesCalls++
            return packageNames
        }

        override suspend fun upsert(models: List<ApplicationModel>) {
            upsertedModels += models
        }

        override suspend fun delete(models: List<ApplicationModel>): Int {
            deletedModels += models
            return models.size
        }

        override suspend fun getApplication(packageName: String): ApplicationModel? =
            applications.firstOrNull { it.packageName == packageName }

        override suspend fun updatePinnedAt(packageName: String, pinnedAt: Long): Int = 0

        override suspend fun count(): Int = applications.size
    }

    private class FakePackageInfoProvider(
        private val installedPackages: List<String> = emptyList(),
        private val applications: Map<String, ApplicationModel?> = emptyMap(),
        private val changedPackages: Map<Int, ChangedPackages?> = emptyMap(),
    ) : PackageInfoProvider {
        val changedPackagesRequests = mutableListOf<Int>()
        val installedPackagesRequests = mutableListOf<String>()
        val applicationRequests = mutableListOf<String>()

        override fun getInstalledPackages(): List<String> {
            installedPackagesRequests += "getInstalledPackages"
            return installedPackages
        }

        override fun getApplication(packageName: String): ApplicationModel? {
            applicationRequests += packageName
            return applications[packageName]
        }

        override fun getActivities(packageName: String): List<ActivityModel> = emptyList()

        override fun getPackageInfo(packageName: String): PackageInfo =
            throw NotImplementedError("Not needed for ApplicationsLoader tests")

        override fun getResourcesForApplication(packageName: String): Resources =
            throw NotImplementedError("Not needed for ApplicationsLoader tests")

        override fun getChangedPackages(lastSequenceNumber: Int): ChangedPackages? {
            changedPackagesRequests += lastSequenceNumber
            return changedPackages[lastSequenceNumber]
        }
    }

    private class FakeAppPreferences(
        override var lastSequenceNumber: Int = -1,
        override var lastBootCount: Int = -1,
    ) : AppPreferences {
        override val preferences: Flow<PreferencesState> = emptyFlow()
        override val displayConfig: Flow<DisplayConfig> = emptyFlow()
        override var isNotExportedDialogShown: Boolean = false
        override val appOpenCounter: Int = 0
        override var isShowSystemApps: Boolean = true
        override var isShowSystemAppIndicator: Boolean = false
        override var isShowDisabledApps: Boolean = true
        override var isShowDisabledAppIndicator: Boolean = false
        override var showNotExported: Boolean = false
        override var showLineNumbers: Boolean = true
        override var theme: Int = 0
        override var sortBy: String = ApplicationModel.NAME
        override var sortOrder: String = "ASC"
        override var suExecutable: String = "su"
        override fun onAppOpened() = Unit
    }

    private class FakeEnvironmentInfoProvider(
        override val isQuickSyncSupported: Boolean = true,
        override val bootCount: Int = 3,
    ) : EnvironmentInfoProvider
}
