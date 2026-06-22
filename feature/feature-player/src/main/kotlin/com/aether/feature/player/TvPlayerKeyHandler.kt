package com.aether.feature.player

import android.view.KeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type

fun androidx.compose.ui.Modifier.tvPlayerKeyEvents(
    onPlayPause: () -> Unit,
    onSeekBack: () -> Unit,
    onSeekForward: () -> Unit,
    onBack: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
): androidx.compose.ui.Modifier = this.onPreviewKeyEvent { keyEvent ->
    if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false
    when (keyEvent.key) {
        Key.MediaPlay, Key.MediaPause, Key.MediaPlayPause -> {
            onPlayPause()
            true
        }
        Key.MediaFastForward, Key.DirectionRight -> {
            onSeekForward()
            true
        }
        Key.MediaRewind, Key.DirectionLeft -> {
            onSeekBack()
            true
        }
        Key.DirectionUp, Key.ChannelUp -> {
            onChannelUp()
            true
        }
        Key.DirectionDown, Key.ChannelDown -> {
            onChannelDown()
            true
        }
        else -> false
    }
}
