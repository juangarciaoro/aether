package com.aether.data.epg

import com.aether.core.database.dao.EpgDao
import com.aether.core.database.dao.ProviderDao
import kotlinx.coroutines.flow.first
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
        val cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000
        epgDao.deleteOldPrograms(cutoff)

        val providers = providerDao.observeAll().first().filter { it.isActive }
        for (provider in providers) {
            try {
                when (provider.type) {
                    "xtream" -> {
                        val baseUrl = provider.url.trimEnd('/')
                        val epgUrl = "$baseUrl/xmltv.php?username=${provider.username}&password=${provider.password}"
                        syncFromUrl(epgUrl)
                    }
                    else -> Unit
                }
                providerDao.updateLastSync(provider.id, System.currentTimeMillis())
            } catch (_: Exception) {
                // Continue with next provider on failure
            }
        }
    }

    suspend fun syncFromUrl(xmltvUrl: String) {
        val request = Request.Builder().url(xmltvUrl).build()
        okHttpClient.newCall(request).execute().use { response ->
            response.body?.byteStream()?.use { stream ->
                val programs = xmltvParser.parse(stream).toList()
                programs.chunked(500).forEach { batch ->
                    epgDao.upsertAll(batch)
                }
            }
        }
    }
}
