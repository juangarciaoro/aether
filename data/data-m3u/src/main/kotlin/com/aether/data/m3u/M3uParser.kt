package com.aether.data.m3u

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject

class M3uParser @Inject constructor(
    private val okHttpClient: OkHttpClient,
) {

    private val tvgIdRegex = Regex("""tvg-id="([^"]*)"""")
    private val tvgNameRegex = Regex("""tvg-name="([^"]*)"""")
    private val tvgLogoRegex = Regex("""tvg-logo="([^"]*)"""")
    private val groupTitleRegex = Regex("""group-title="([^"]*)"""")
    private val catchupRegex = Regex("""catchup="([^"]*)"""")
    private val catchupSourceRegex = Regex("""catchup-source="([^"]*)"""")
    private val catchupDaysRegex = Regex("""catchup-days="(\d+)"""")

    fun parseFromUrl(url: String): Flow<M3uChannel> = flow {
        val request = Request.Builder().url(url).build()
        val response = okHttpClient.newCall(request).execute()
        response.body?.byteStream()?.let { stream ->
            parseStream(stream).collect { emit(it) }
        } ?: throw IllegalStateException("Empty response from $url")
    }.flowOn(Dispatchers.IO)

    fun parseFromStream(inputStream: InputStream): Flow<M3uChannel> =
        parseStream(inputStream).flowOn(Dispatchers.IO)

    private fun parseStream(inputStream: InputStream): Flow<M3uChannel> = flow {
        val reader = BufferedReader(InputStreamReader(inputStream))
        var currentExtInf: String? = null
        var currentVlcOpts = mutableMapOf<String, String>()

        reader.useLines { lines ->
            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("#EXTINF:") -> {
                        currentExtInf = trimmed
                        currentVlcOpts = mutableMapOf()
                    }
                    trimmed.startsWith("#EXTVLCOPT:") -> {
                        val opt = trimmed.removePrefix("#EXTVLCOPT:").trim()
                        val parts = opt.split("=", limit = 2)
                        if (parts.size == 2) currentVlcOpts[parts[0]] = parts[1]
                    }
                    trimmed.isNotEmpty() && !trimmed.startsWith("#") && currentExtInf != null -> {
                        val channel = buildChannel(currentExtInf!!, trimmed, currentVlcOpts)
                        emit(channel)
                        currentExtInf = null
                        currentVlcOpts = mutableMapOf()
                    }
                }
            }
        }
    }

    private fun buildChannel(
        extinf: String,
        streamUrl: String,
        vlcOpts: Map<String, String>,
    ): M3uChannel {
        val displayName = extinf.substringAfterLast(",").trim()
        return M3uChannel(
            name = displayName,
            streamUrl = streamUrl,
            tvgId = tvgIdRegex.find(extinf)?.groupValues?.getOrNull(1) ?: "",
            tvgName = tvgNameRegex.find(extinf)?.groupValues?.getOrNull(1) ?: displayName,
            tvgLogo = tvgLogoRegex.find(extinf)?.groupValues?.getOrNull(1) ?: "",
            groupTitle = groupTitleRegex.find(extinf)?.groupValues?.getOrNull(1) ?: "",
            catchup = catchupRegex.find(extinf)?.groupValues?.getOrNull(1) ?: "",
            catchupSource = catchupSourceRegex.find(extinf)?.groupValues?.getOrNull(1) ?: "",
            catchupDays = catchupDaysRegex.find(extinf)?.groupValues?.getOrNull(1)?.toIntOrNull() ?: 0,
            vlcOptions = vlcOpts,
        )
    }
}
