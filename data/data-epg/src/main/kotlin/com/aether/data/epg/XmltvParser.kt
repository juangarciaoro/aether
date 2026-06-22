package com.aether.data.epg

import android.util.Xml
import com.aether.core.database.entity.EpgProgramEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class XmltvParser @Inject constructor() {

    private val xmltvDateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun parse(inputStream: InputStream): Flow<EpgProgramEntity> = flow {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var programBuilder: EpgProgramBuilder? = null
        var currentTag: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    when (parser.name) {
                        "programme" -> {
                            programBuilder = EpgProgramBuilder(
                                channelId = parser.getAttributeValue(null, "channel") ?: "",
                                startTime = parser.getAttributeValue(null, "start")?.parseXmltvDate() ?: 0L,
                                stopTime = parser.getAttributeValue(null, "stop")?.parseXmltvDate() ?: 0L,
                            )
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    programBuilder?.let { builder ->
                        val text = parser.text?.trim() ?: ""
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "title" -> builder.title = text
                                "desc" -> builder.description = text
                                "category" -> builder.categories.add(text)
                                "new" -> builder.isNew = true
                            }
                        }
                    }
                }
                XmlPullParser.START_TAG -> {
                    if (parser.name == "icon") {
                        programBuilder?.iconUrl = parser.getAttributeValue(null, "src") ?: ""
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "programme") {
                        programBuilder?.build()?.let { emit(it) }
                        programBuilder = null
                        currentTag = null
                    }
                }
            }
            eventType = parser.next()
        }
    }.flowOn(Dispatchers.IO)

    private fun String.parseXmltvDate(): Long = try {
        xmltvDateFormat.parse(this.take(14) + " +0000")?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

private class EpgProgramBuilder(
    val channelId: String,
    val startTime: Long,
    val stopTime: Long,
) {
    var title: String = ""
    var description: String = ""
    var iconUrl: String = ""
    var categories: MutableList<String> = mutableListOf()
    var isNew: Boolean = false

    fun build(): EpgProgramEntity = EpgProgramEntity(
        id = "${channelId}_${startTime}",
        channelTvgId = channelId,
        title = title,
        description = description,
        startTime = startTime,
        endTime = stopTime,
        iconUrl = iconUrl,
        categories = categories.joinToString(","),
        isNew = isNew,
    )
}
