package com.aether.data.xtream

import com.aether.core.common.result.AetherResult
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.ProviderDao
import com.aether.core.database.dao.VodDao
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.VodEntity
import com.aether.data.xtream.api.XtreamApi
import com.aether.data.xtream.model.XtreamAuthResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XtreamRepository @Inject constructor(
    private val api: XtreamApi,
    private val channelDao: ChannelDao,
    private val vodDao: VodDao,
    private val providerDao: ProviderDao,
) {

    suspend fun authenticate(
        baseUrl: String,
        username: String,
        password: String,
    ): AetherResult<XtreamAuthResponse> = runCatching {
        val playerApiUrl = "$baseUrl/player_api.php"
        api.authenticate(playerApiUrl, username, password)
    }.fold(
        onSuccess = { AetherResult.Success(it) },
        onFailure = { AetherResult.Error(it) },
    )

    suspend fun syncLiveStreams(
        providerId: Long,
        baseUrl: String,
        username: String,
        password: String,
    ): AetherResult<Int> = runCatching {
        val playerApiUrl = "$baseUrl/player_api.php"
        val streams = api.getLiveStreams(playerApiUrl, username, password)
        val entities = streams.mapIndexed { index, stream ->
            ChannelEntity(
                id = "${providerId}_live_${stream.streamId}",
                providerId = providerId,
                streamId = stream.streamId,
                name = stream.name,
                streamUrl = "$baseUrl/live/$username/$password/${stream.streamId}.${stream.containerExtension}",
                logoUrl = stream.streamIcon,
                categoryId = stream.categoryId,
                tvgId = stream.epgChannelId,
                tvgName = stream.name,
                streamType = "live",
                containerExtension = stream.containerExtension,
                catchupDays = stream.tvArchiveDuration,
                sortOrder = stream.num,
            )
        }
        channelDao.upsertAll(entities)
        entities.size
    }.fold(
        onSuccess = { AetherResult.Success(it) },
        onFailure = { AetherResult.Error(it) },
    )

    suspend fun syncVod(
        providerId: Long,
        baseUrl: String,
        username: String,
        password: String,
    ): AetherResult<Int> = runCatching {
        val playerApiUrl = "$baseUrl/player_api.php"
        val vods = api.getVodStreams(playerApiUrl, username, password)
        val entities = vods.map { vod ->
            VodEntity(
                id = "${providerId}_vod_${vod.streamId}",
                providerId = providerId,
                vodId = vod.streamId,
                name = vod.name,
                streamUrl = "$baseUrl/movie/$username/$password/${vod.streamId}.${vod.containerExtension}",
                posterUrl = vod.streamIcon,
                categoryId = vod.categoryId,
                containerExtension = vod.containerExtension,
                rating = vod.rating,
            )
        }
        vodDao.upsertAll(entities)
        entities.size
    }.fold(
        onSuccess = { AetherResult.Success(it) },
        onFailure = { AetherResult.Error(it) },
    )

    fun buildLiveUrl(baseUrl: String, username: String, password: String, streamId: Int, ext: String = "ts") =
        "$baseUrl/live/$username/$password/$streamId.$ext"

    fun buildVodUrl(baseUrl: String, username: String, password: String, vodId: Int, ext: String = "mp4") =
        "$baseUrl/movie/$username/$password/$vodId.$ext"

    fun buildSeriesUrl(baseUrl: String, username: String, password: String, episodeId: String, ext: String = "mp4") =
        "$baseUrl/series/$username/$password/$episodeId.$ext"

    fun buildCatchupUrl(baseUrl: String, username: String, password: String, streamId: Int, duration: Int, start: String) =
        "$baseUrl/timeshift/$username/$password/$duration/$start/$streamId.ts"
}
