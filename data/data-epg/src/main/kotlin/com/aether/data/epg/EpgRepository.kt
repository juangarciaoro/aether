package com.aether.data.epg

import com.aether.core.database.dao.EpgDao
import com.aether.core.database.dao.ProviderDao
import kotlinx.coroutines.flow.toList
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpgRepository @Inject constructor(
    private val epgDao: EpgDao,
    private val providerDao: ProviderDao,
    private val xmltvParser: XmltvParser,
    private val okHttpClient: OkHttpClient,
) {

    suspend fun syncAll() {
        // Clean up old entries (older than 1 day)
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        epgDao.deleteOldPrograms(cutoff)
    }

    suspend fun syncFromUrl(xmltvUrl: String) {
        val request = Request.Builder().url(xmltvUrl).build()
        val response = okHttpClient.newCall(request).execute()
        response.body?.byteStream()?.use { stream ->
            val programs = xmltvParser.parse(stream).toList()
            val batchSize = 500
            programs.chunked(batchSize).forEach { batch ->
                epgDao.upsertAll(batch)
            }
        }
    }
}
