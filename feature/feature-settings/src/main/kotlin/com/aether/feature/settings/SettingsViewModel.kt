package com.aether.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val pipAutoEnter: Boolean = true,
    val resumePlayback: Boolean = true,
    val subtitleEnabled: Boolean = false,
    val epgDaysToKeep: Int = 3,
    val playerBufferSeconds: Int = 30,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState = combine(
        prefsRepository.pipAutoEnter,
        prefsRepository.resumePlayback,
        prefsRepository.subtitleEnabled,
        prefsRepository.epgDaysToKeep,
        prefsRepository.playerBufferSeconds,
    ) { pipAutoEnter, resumePlayback, subtitleEnabled, epgDays, bufferSecs ->
        SettingsUiState(
            pipAutoEnter = pipAutoEnter,
            resumePlayback = resumePlayback,
            subtitleEnabled = subtitleEnabled,
            epgDaysToKeep = epgDays,
            playerBufferSeconds = bufferSecs,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState(),
    )

    fun setPipAutoEnter(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setPipAutoEnter(enabled) }
    }

    fun setResumePlayback(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setResumePlayback(enabled) }
    }

    fun setSubtitleEnabled(enabled: Boolean) {
        viewModelScope.launch { prefsRepository.setSubtitleEnabled(enabled) }
    }
}
