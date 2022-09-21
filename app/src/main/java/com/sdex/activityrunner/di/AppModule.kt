package com.sdex.activityrunner.di

import android.content.Context
import com.sdex.activityrunner.preferences.AppPreferences
import com.sdex.activityrunner.util.PackageInfoProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePreferences(
        @ApplicationContext context: Context
    ) = AppPreferences(context)

    @Provides
    fun providePackageInfoProvider(
        @ApplicationContext context: Context
    ) = PackageInfoProvider(context)
}
