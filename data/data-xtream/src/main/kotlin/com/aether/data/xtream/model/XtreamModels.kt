package com.aether.data.xtream.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class XtreamAuthResponse(
    @SerialName("user_info") val userInfo: XtreamUserInfo? = null,
    @SerialName("server_info") val serverInfo: XtreamServerInfo? = null,
)

@Serializable
data class XtreamUserInfo(
    @SerialName("username") val username: String = "",
    @SerialName("status") val status: String = "",
    @SerialName("exp_date") val expDate: String? = null,
    @SerialName("max_connections") val maxConnections: String = "1",
    @SerialName("active_cons") val activeConnections: String = "0",
)

@Serializable
data class XtreamServerInfo(
    @SerialName("url") val url: String = "",
    @SerialName("port") val port: String = "",
    @SerialName("https_port") val httpsPort: String = "",
    @SerialName("server_protocol") val serverProtocol: String = "http",
    @SerialName("rtmp_port") val rtmpPort: String = "",
    @SerialName("timezone") val timezone: String = "",
    @SerialName("timestamp_now") val timestampNow: Long = 0,
)

@Serializable
data class XtreamCategory(
    @SerialName("category_id") val categoryId: String = "",
    @SerialName("category_name") val categoryName: String = "",
    @SerialName("parent_id") val parentId: Int = 0,
)

@Serializable
data class XtreamStream(
    @SerialName("stream_id") val streamId: Int = 0,
    @SerialName("num") val num: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("stream_icon") val streamIcon: String = "",
    @SerialName("category_id") val categoryId: String = "",
    @SerialName("category_ids") val categoryIds: List<String> = emptyList(),
    @SerialName("epg_channel_id") val epgChannelId: String = "",
    @SerialName("stream_type") val streamType: String = "live",
    @SerialName("tv_archive") val tvArchive: Int = 0,
    @SerialName("tv_archive_duration") val tvArchiveDuration: Int = 0,
    @SerialName("container_extension") val containerExtension: String = "ts",
)

@Serializable
data class XtreamVod(
    @SerialName("stream_id") val streamId: Int = 0,
    @SerialName("num") val num: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("stream_icon") val streamIcon: String = "",
    @SerialName("category_id") val categoryId: String = "",
    @SerialName("container_extension") val containerExtension: String = "mp4",
    @SerialName("added") val added: String = "",
    @SerialName("rating") val rating: String = "",
)

@Serializable
data class XtreamVodInfo(
    @SerialName("info") val info: XtreamVodDetail? = null,
    @SerialName("movie_data") val movieData: XtreamVodData? = null,
)

@Serializable
data class XtreamVodDetail(
    @SerialName("name") val name: String = "",
    @SerialName("o_name") val originalName: String = "",
    @SerialName("cover_big") val coverBig: String = "",
    @SerialName("movie_image") val movieImage: String = "",
    @SerialName("releasedate") val releaseDate: String = "",
    @SerialName("episode_run_time") val episodeRunTime: String = "",
    @SerialName("youtube_trailer") val youtubeTrailer: String = "",
    @SerialName("director") val director: String = "",
    @SerialName("actors") val actors: String = "",
    @SerialName("cast") val cast: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("plot") val plot: String = "",
    @SerialName("age") val age: String = "",
    @SerialName("country") val country: String = "",
    @SerialName("genre") val genre: String = "",
    @SerialName("backdrop_path") val backdropPath: List<String> = emptyList(),
    @SerialName("duration_secs") val durationSecs: Int = 0,
    @SerialName("duration") val duration: String = "",
    @SerialName("rating") val rating: String = "",
    @SerialName("rating_5based") val rating5Based: Double = 0.0,
)

@Serializable
data class XtreamVodData(
    @SerialName("stream_id") val streamId: Int = 0,
    @SerialName("container_extension") val containerExtension: String = "mp4",
)

@Serializable
data class XtreamSeries(
    @SerialName("series_id") val seriesId: Int = 0,
    @SerialName("num") val num: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("category_id") val categoryId: String = "",
    @SerialName("rating") val rating: String = "",
)

@Serializable
data class XtreamSeriesInfo(
    @SerialName("info") val info: XtreamSeriesDetail? = null,
    @SerialName("episodes") val episodes: Map<String, List<XtreamEpisode>> = emptyMap(),
    @SerialName("seasons") val seasons: List<XtreamSeason> = emptyList(),
)

@Serializable
data class XtreamSeriesDetail(
    @SerialName("name") val name: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("backdrop_path") val backdropPath: List<String> = emptyList(),
    @SerialName("genre") val genre: String = "",
    @SerialName("plot") val plot: String = "",
    @SerialName("cast") val cast: String = "",
    @SerialName("director") val director: String = "",
    @SerialName("releaseDate") val releaseDate: String = "",
    @SerialName("rating") val rating: String = "",
    @SerialName("episode_run_time") val episodeRunTime: String = "",
)

@Serializable
data class XtreamEpisode(
    @SerialName("id") val id: String = "",
    @SerialName("episode_num") val episodeNum: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("container_extension") val containerExtension: String = "mp4",
    @SerialName("season") val season: Int = 0,
    @SerialName("info") val info: XtreamEpisodeInfo? = null,
)

@Serializable
data class XtreamEpisodeInfo(
    @SerialName("name") val name: String = "",
    @SerialName("plot") val plot: String = "",
    @SerialName("duration_secs") val durationSecs: Int = 0,
    @SerialName("rating") val rating: String = "",
    @SerialName("movie_image") val movieImage: String = "",
)

@Serializable
data class XtreamSeason(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("season_number") val seasonNumber: Int = 0,
    @SerialName("cover") val cover: String = "",
    @SerialName("cover_big") val coverBig: String = "",
    @SerialName("episode_count") val episodeCount: Int = 0,
    @SerialName("air_date") val airDate: String = "",
)

@Serializable
data class XtreamEpgResponse(
    @SerialName("epg_listings") val epgListings: List<XtreamEpgListing> = emptyList(),
)

@Serializable
data class XtreamEpgListing(
    @SerialName("id") val id: String = "",
    @SerialName("epg_id") val epgId: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("lang") val lang: String = "",
    @SerialName("start") val start: String = "",
    @SerialName("end") val end: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("channel_id") val channelId: String = "",
    @SerialName("start_timestamp") val startTimestamp: Long = 0,
    @SerialName("stop_timestamp") val stopTimestamp: Long = 0,
)
