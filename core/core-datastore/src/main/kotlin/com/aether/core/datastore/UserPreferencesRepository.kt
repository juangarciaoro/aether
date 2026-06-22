package com.aether.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aether_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val PREFERRED_AUDIO_LANG = stringPreferencesKey("preferred_audio_lang")
        val PREFERRED_SUBTITLE_LANG = stringPreferencesKey("preferred_subtitle_lang")
        val SUBTITLE_ENABLED = booleanPreferencesKey("subtitle_enabled")
        val SUBTITLE_FONT_SIZE = floatPreferencesKey("subtitle_font_size")
        val SUBTITLE_COLOR = longPreferencesKey("subtitle_color")
        val SUBTITLE_BG_COLOR = longPreferencesKey("subtitle_bg_color")
        val SUBTITLE_POSITION = stringPreferencesKey("subtitle_position")
        val PLAYER_DECODER = stringPreferencesKey("player_decoder")
        val PLAYER_BUFFER_SECS = intPreferencesKey("player_buffer_secs")
        val PIP_AUTO_ENTER = booleanPreferencesKey("pip_auto_enter")
        val RESUME_PLAYBACK = booleanPreferencesKey("resume_playback")
        val MAX_MULTISCREEN = intPreferencesKey("max_multiscreen")
        val EPG_DAYS_TO_KEEP = intPreferencesKey("epg_days_to_keep")
        val UI_SCALE = stringPreferencesKey("ui_scale")
    }

    val onboardingComplete: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] ?: false }

    val preferredAudioLanguage: Flow<String> =
        context.dataStore.data.map { it[Keys.PREFERRED_AUDIO_LANG] ?: "spa" }

    val subtitleEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.SUBTITLE_ENABLED] ?: false }

    val pipAutoEnter: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.PIP_AUTO_ENTER] ?: true }

    val resumePlayback: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.RESUME_PLAYBACK] ?: true }

    val playerBufferSeconds: Flow<Int> =
        context.dataStore.data.map { it[Keys.PLAYER_BUFFER_SECS] ?: 30 }

    val epgDaysToKeep: Flow<Int> =
        context.dataStore.data.map { it[Keys.EPG_DAYS_TO_KEEP] ?: 3 }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setPreferredAudioLanguage(lang: String) {
        context.dataStore.edit { it[Keys.PREFERRED_AUDIO_LANG] = lang }
    }

    suspend fun setSubtitleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.SUBTITLE_ENABLED] = enabled }
    }

    suspend fun setPipAutoEnter(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PIP_AUTO_ENTER] = enabled }
    }

    suspend fun setResumePlayback(enabled: Boolean) {
        context.dataStore.edit { it[Keys.RESUME_PLAYBACK] = enabled }
    }

    suspend fun setPlayerBufferSeconds(seconds: Int) {
        context.dataStore.edit { it[Keys.PLAYER_BUFFER_SECS] = seconds }
    }

    suspend fun setEpgDaysToKeep(days: Int) {
        context.dataStore.edit { it[Keys.EPG_DAYS_TO_KEEP] = days }
    }
}
