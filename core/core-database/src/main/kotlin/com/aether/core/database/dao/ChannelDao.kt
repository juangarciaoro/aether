package com.aether.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.aether.core.database.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Upsert
    suspend fun upsertAll(channels: List<ChannelEntity>)

    @Query("SELECT * FROM channels WHERE providerId = :providerId ORDER BY sortOrder ASC")
    fun observeByProvider(providerId: Long): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE categoryId = :categoryId ORDER BY sortOrder ASC")
    fun observeByCategory(categoryId: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' OR tvgName LIKE '%' || :query || '%' LIMIT 50")
    fun search(query: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getById(id: String): ChannelEntity?

    @Query("DELETE FROM channels WHERE providerId = :providerId")
    suspend fun deleteByProvider(providerId: Long)

    @Query("SELECT COUNT(*) FROM channels WHERE providerId = :providerId")
    suspend fun countByProvider(providerId: Long): Int
}
