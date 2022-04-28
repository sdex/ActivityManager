package com.sdex.activityrunner.di

import android.content.Context
import com.sdex.activityrunner.db.cache.CacheDatabase
import com.sdex.activityrunner.db.history.HistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCacheDatabase(
        @ApplicationContext context: Context
    ) = CacheDatabase.getDatabase(context)

    @Provides
    fun provideApplicationDao(database: CacheDatabase) = database.applicationDao

    @Provides
    @Singleton
    fun provideHistoryDatabase(
        @ApplicationContext context: Context
    ) = HistoryDatabase.getDatabase(context)

    @Provides
    fun provideHistoryDao(database: HistoryDatabase) = database.historyDao
}