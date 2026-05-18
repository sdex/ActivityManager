package com.sdex.activityrunner.di

import android.content.Context
import com.sdex.activityrunner.commons.platform.EnvironmentInfoProvider
import com.sdex.activityrunner.db.cache.CacheRepository
import com.sdex.activityrunner.manifest.ManifestWriter
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.preferences.AppPreferencesImpl
import com.sdex.activityrunner.util.ApplicationsLoader
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferences(
        @ApplicationContext context: Context,
    ): AppPreferences = AppPreferencesImpl(context)

    @Provides
    fun provideManifestWriter(
        @ApplicationContext context: Context,
    ) = ManifestWriter(context)

    @Provides
    @Singleton
    fun provideGlobalCoroutineScope() =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Provides
    @Singleton
    fun provideApplicationsLoader(
        cacheRepository: CacheRepository,
        packageInfoProvider: PackageInfoProvider,
        preferences: AppPreferences,
        environmentInfoProvider: EnvironmentInfoProvider,
    ) = ApplicationsLoader(
        cacheRepository = cacheRepository,
        packageInfoProvider = packageInfoProvider,
        preferences = preferences,
        environmentInfoProvider = environmentInfoProvider,
    )
}
