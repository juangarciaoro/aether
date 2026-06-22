package com.aether.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "providers")
data class ProviderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "xtream" | "m3u"
    val url: String,
    val username: String = "",
    val password: String = "",
    val isActive: Boolean = true,
    val lastSyncAt: Long = 0,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val providerId: Long,
    val name: String,
    val type: String, // "live" | "vod" | "series"
    val sortOrder: Int = 0,
)

@Entity(
    tableName = "channels",
    indices = [Index("providerId"), Index("categoryId"), Index("tvgId")],
)
data class ChannelEntity(
    @PrimaryKey val id: String,
    val providerId: Long,
    val streamId: Int,
    val name: String,
    val streamUrl: String,
    val logoUrl: String = "",
    val categoryId: String = "",
    val tvgId: String = "",
    val tvgName: String = "",
    val streamType: String = "live",
    val containerExtension: String = "ts",
    val catchupSource: String = "",
    val catchupDays: Int = 0,
    val sortOrder: Int = 0,
    val addedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "epg_programs",
    indices = [Index("channelTvgId"), Index("startTime")],
)
data class EpgProgramEntity(
    @PrimaryKey val id: String,
    val channelTvgId: String,
    val title: String,
    val description: String = "",
    val startTime: Long,
    val endTime: Long,
    val iconUrl: String = "",
    val categories: String = "",
    val isNew: Boolean = false,
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val contentId: String,
    val contentType: String, // "live" | "vod" | "series"
    val title: String,
    val imageUrl: String = "",
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val watchedAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val contentId: String,
    val contentType: String,
    val name: String,
    val imageUrl: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "vod",
    indices = [Index("providerId"), Index("categoryId")],
)
data class VodEntity(
    @PrimaryKey val id: String,
    val providerId: Long,
    val vodId: Int,
    val name: String,
    val streamUrl: String,
    val posterUrl: String = "",
    val backdropUrl: String = "",
    val categoryId: String = "",
    val year: String = "",
    val rating: String = "",
    val duration: String = "",
    val plot: String = "",
    val director: String = "",
    val cast: String = "",
    val genre: String = "",
    val containerExtension: String = "mp4",
    val addedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "series",
    indices = [Index("providerId"), Index("categoryId")],
)
data class SeriesEntity(
    @PrimaryKey val id: String,
    val providerId: Long,
    val seriesId: Int,
    val name: String,
    val coverUrl: String = "",
    val backdropUrl: String = "",
    val categoryId: String = "",
    val year: String = "",
    val rating: String = "",
    val plot: String = "",
    val genre: String = "",
    val cast: String = "",
    val director: String = "",
    val episodeRunTime: String = "",
    val addedAt: Long = System.currentTimeMillis(),
)
