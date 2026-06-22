package com.aether.data.m3u

data class M3uChannel(
    val name: String,
    val streamUrl: String,
    val tvgId: String = "",
    val tvgName: String = "",
    val tvgLogo: String = "",
    val groupTitle: String = "",
    val catchup: String = "",
    val catchupSource: String = "",
    val catchupDays: Int = 0,
    val vlcOptions: Map<String, String> = emptyMap(),
)
