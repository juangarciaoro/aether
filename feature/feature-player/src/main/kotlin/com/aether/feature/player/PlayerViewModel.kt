package com.aether.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aether.core.database.dao.WatchHistoryDao
import com.aether.core.database.entity.WatchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val showControls: Boolean = true,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null,
    val isMultiScreen: Boolean = false,
    val multiScreenStreams: List<StreamSlot> = emptyList(),
    val activeMultiScreenIndex: Int = 0,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private var player: ExoPlayer? = null
    private val controlsHideDelay = 3_000L
    private var currentStreamUrl: String = ""
    private var currentStreamTitle: String = ""
    private var progressSaveJob: Job? = null

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            _uiState.update {
                it.copy(isLoading = playbackState == Player.STATE_BUFFERING)
            }
        }

        override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
            _uiState.update { it.copy(error = error.message) }
        }
    }

    fun setPlayer(exoPlayer: ExoPlayer) {
        player = exoPlayer
        exoPlayer.addListener(playerListener)
    }

    fun clearPlayer() {
        progressSaveJob?.cancel()
        saveProgress()
        player?.removeListener(playerListener)
        player = null
    }

    fun loadStream(url: String, title: String = "") {
        currentStreamUrl = url
        currentStreamTitle = title.ifBlank { url.substringAfterLast("/").substringBefore("?") }
        player?.let {
            it.setMediaItem(MediaItem.fromUri(url))
            it.prepare()
            it.playWhenReady = true
        }
        startProgressTracking()
    }

    private fun startProgressTracking() {
        progressSaveJob?.cancel()
        progressSaveJob = viewModelScope.launch {
            while (isActive) {
                delay(30_000)
                saveProgress()
            }
        }
    }

    private fun saveProgress() {
        val p = player ?: return
        if (currentStreamUrl.isBlank()) return
        viewModelScope.launch {
            watchHistoryDao.upsert(
                WatchHistoryEntity(
                    contentId = currentStreamUrl,
                    contentType = "live",
                    title = currentStreamTitle,
                    positionMs = p.currentPosition,
                    durationMs = p.duration.coerceAtLeast(0),
                    watchedAt = System.currentTimeMillis(),
                )
            )
        }
    }

    fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
        showControlsTemporarily()
    }

    fun seekRelative(offsetMs: Long) {
        player?.let {
            it.seekTo((it.currentPosition + offsetMs).coerceAtLeast(0))
        }
        showControlsTemporarily()
    }

    fun showControlsTemporarily() {
        _uiState.update { it.copy(showControls = true) }
        viewModelScope.launch {
            delay(controlsHideDelay)
            _uiState.update { it.copy(showControls = false) }
        }
    }

    fun toggleMultiScreen(currentUrl: String) {
        _uiState.update { state ->
            if (state.isMultiScreen) {
                state.copy(isMultiScreen = false, multiScreenStreams = emptyList())
            } else {
                state.copy(
                    isMultiScreen = true,
                    multiScreenStreams = listOf(StreamSlot(url = currentUrl)),
                    activeMultiScreenIndex = 0,
                )
            }
        }
    }

    fun addMultiScreenStream(url: String) {
        _uiState.update { state ->
            if (state.multiScreenStreams.size >= 4) return@update state
            state.copy(multiScreenStreams = state.multiScreenStreams + StreamSlot(url = url))
        }
    }

    fun removeMultiScreenStream(index: Int) {
        _uiState.update { state ->
            val newStreams = state.multiScreenStreams.toMutableList().apply { removeAt(index) }
            state.copy(
                multiScreenStreams = newStreams,
                activeMultiScreenIndex = if (state.activeMultiScreenIndex >= newStreams.size) 0 else state.activeMultiScreenIndex,
                isMultiScreen = newStreams.isNotEmpty(),
            )
        }
    }

    fun selectMultiScreenStream(index: Int) {
        _uiState.update { it.copy(activeMultiScreenIndex = index) }
    }
}
