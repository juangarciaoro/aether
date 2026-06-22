package com.aether.data.xtream.api

import com.aether.data.xtream.model.XtreamAuthResponse
import com.aether.data.xtream.model.XtreamCategory
import com.aether.data.xtream.model.XtreamEpgResponse
import com.aether.data.xtream.model.XtreamSeries
import com.aether.data.xtream.model.XtreamSeriesInfo
import com.aether.data.xtream.model.XtreamStream
import com.aether.data.xtream.model.XtreamVod
import com.aether.data.xtream.model.XtreamVodInfo
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

interface XtreamApi {

    @GET
    suspend fun authenticate(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
    ): XtreamAuthResponse

    @GET
    suspend fun getLiveCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_categories",
    ): List<XtreamCategory>

    @GET
    suspend fun getLiveStreams(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_live_streams",
        @Query("category_id") categoryId: String? = null,
    ): List<XtreamStream>

    @GET
    suspend fun getEpgForStream(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_simple_data_table",
        @Query("stream_id") streamId: Int,
    ): XtreamEpgResponse

    @GET
    @Streaming
    suspend fun getXmltvEpg(@Url xmltvUrl: String): ResponseBody

    @GET
    suspend fun getVodCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_categories",
    ): List<XtreamCategory>

    @GET
    suspend fun getVodStreams(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_streams",
        @Query("category_id") categoryId: String? = null,
    ): List<XtreamVod>

    @GET
    suspend fun getVodInfo(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_vod_info",
        @Query("vod_id") vodId: Int,
    ): XtreamVodInfo

    @GET
    suspend fun getSeriesCategories(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_categories",
    ): List<XtreamCategory>

    @GET
    suspend fun getSeries(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series",
        @Query("category_id") categoryId: String? = null,
    ): List<XtreamSeries>

    @GET
    suspend fun getSeriesInfo(
        @Url url: String,
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("action") action: String = "get_series_info",
        @Query("series_id") seriesId: Int,
    ): XtreamSeriesInfo
}
