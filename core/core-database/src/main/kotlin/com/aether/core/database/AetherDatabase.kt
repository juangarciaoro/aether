package com.aether.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aether.core.database.dao.CategoryDao
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.EpgDao
import com.aether.core.database.dao.FavoriteDao
import com.aether.core.database.dao.ProviderDao
import com.aether.core.database.dao.SeriesDao
import com.aether.core.database.dao.VodDao
import com.aether.core.database.dao.WatchHistoryDao
import com.aether.core.database.entity.CategoryEntity
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.EpgProgramEntity
import com.aether.core.database.entity.FavoriteEntity
import com.aether.core.database.entity.ProviderEntity
import com.aether.core.database.entity.SeriesEntity
import com.aether.core.database.entity.VodEntity
import com.aether.core.database.entity.WatchHistoryEntity
import com.aether.core.database.util.StringListConverter

@Database(
    entities = [
        ChannelEntity::class,
        EpgProgramEntity::class,
        WatchHistoryEntity::class,
        FavoriteEntity::class,
        ProviderEntity::class,
        CategoryEntity::class,
        VodEntity::class,
        SeriesEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(StringListConverter::class)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun epgDao(): EpgDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun providerDao(): ProviderDao
    abstract fun categoryDao(): CategoryDao
    abstract fun vodDao(): VodDao
    abstract fun seriesDao(): SeriesDao
}
