package com.aether.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aether.core.database.entity.CategoryEntity
import com.aether.core.database.entity.FavoriteEntity
import com.aether.core.database.entity.ProviderEntity
import com.aether.core.database.entity.SeriesEntity
import com.aether.core.database.entity.VodEntity
import com.aether.core.database.entity.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderDao {
    @Upsert
    suspend fun upsert(provider: ProviderEntity): Long

    @Query("SELECT * FROM providers ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE id = :id")
    suspend fun getById(id: Long): ProviderEntity?

    @Query("DELETE FROM providers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE providers SET lastSyncAt = :timestamp WHERE id = :id")
    suspend fun updateLastSync(id: Long, timestamp: Long)
}

@Dao
interface WatchHistoryDao {
    @Upsert
    suspend fun upsert(entry: WatchHistoryEntity)

    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC LIMIT 30")
    fun observeRecent(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE contentId = :id")
    suspend fun getById(id: String): WatchHistoryEntity?

    @Query("DELETE FROM watch_history WHERE contentId = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM watch_history WHERE watchedAt < :before")
    suspend fun deleteOlderThan(before: Long)
}

@Dao
interface FavoriteDao {
    @Upsert
    suspend fun upsert(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE contentId = :id)")
    fun observeIsFavorite(id: String): Flow<Boolean>

    @Query("DELETE FROM favorites WHERE contentId = :id")
    suspend fun deleteById(id: String)
}

@Dao
interface VodDao {
    @Upsert
    suspend fun upsertAll(vods: List<VodEntity>)

    @Query("SELECT * FROM vod WHERE providerId = :providerId ORDER BY name ASC")
    fun observeByProvider(providerId: Long): Flow<List<VodEntity>>

    @Query("SELECT * FROM vod WHERE categoryId = :categoryId ORDER BY name ASC")
    fun observeByCategory(categoryId: String): Flow<List<VodEntity>>

    @Query("SELECT * FROM vod WHERE name LIKE '%' || :query || '%' LIMIT 50")
    fun search(query: String): Flow<List<VodEntity>>

    @Query("SELECT * FROM vod WHERE id = :id")
    suspend fun getById(id: String): VodEntity?

    @Query("DELETE FROM vod WHERE providerId = :providerId")
    suspend fun deleteByProvider(providerId: Long)
}

@Dao
interface SeriesDao {
    @Upsert
    suspend fun upsertAll(series: List<SeriesEntity>)

    @Query("SELECT * FROM series WHERE providerId = :providerId ORDER BY name ASC")
    fun observeByProvider(providerId: Long): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE name LIKE '%' || :query || '%' LIMIT 50")
    fun search(query: String): Flow<List<SeriesEntity>>

    @Query("SELECT * FROM series WHERE id = :id")
    suspend fun getById(id: String): SeriesEntity?

    @Query("DELETE FROM series WHERE providerId = :providerId")
    suspend fun deleteByProvider(providerId: Long)
}
