package com.sdex.activityrunner.di

import android.content.Context
import com.sdex.activityrunner.commons.platform.EnvironmentInfoProvider
import com.sdex.activityrunner.commons.platform.EnvironmentInfoProviderImpl
import com.sdex.activityrunner.util.PackageInfoProvider
import com.sdex.activityrunner.util.PackageInfoProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    @Singleton
    fun provideEnvironmentInfoProvider(
        @ApplicationContext context: Context,
    ): EnvironmentInfoProvider = EnvironmentInfoProviderImpl(context = context)


    @Provides
    @Singleton
    fun providePackageInfoProvider(
        @ApplicationContext context: Context,
    ): PackageInfoProvider = PackageInfoProviderImpl(context = context)
}
