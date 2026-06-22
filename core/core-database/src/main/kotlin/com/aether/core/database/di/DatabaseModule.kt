package com.aether.core.database.di

import android.content.Context
import androidx.room.Room
import com.aether.core.database.AetherDatabase
import com.aether.core.database.dao.CategoryDao
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
    fun providesAetherDatabase(@ApplicationContext context: Context): AetherDatabase =
        Room.databaseBuilder(context, AetherDatabase::class.java, "aether.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun providesChannelDao(db: AetherDatabase) = db.channelDao()
    @Provides fun providesEpgDao(db: AetherDatabase) = db.epgDao()
    @Provides fun providesWatchHistoryDao(db: AetherDatabase) = db.watchHistoryDao()
    @Provides fun providesFavoriteDao(db: AetherDatabase) = db.favoriteDao()
    @Provides fun providesProviderDao(db: AetherDatabase) = db.providerDao()
    @Provides fun providesCategoryDao(db: AetherDatabase): CategoryDao = db.categoryDao()
    @Provides fun providesVodDao(db: AetherDatabase) = db.vodDao()
    @Provides fun providesSeriesDao(db: AetherDatabase) = db.seriesDao()
}
