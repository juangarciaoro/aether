package com.aether.feature.player

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.aether.core.player.buildAetherPlayer
import com.aether.core.ui.theme.NeonIndigo

data class StreamSlot(
    val url: String,
    val label: String = "",
)

@Composable
fun MultiScreenLayout(
    streams: List<StreamSlot>,
    activeIndex: Int,
    onStreamSelect: (Int) -> Unit,
    onRemoveStream: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(modifier = modifier.fillMaxSize().background(Color.Black)) {
        val rows = if (streams.size <= 2) 1 else 2
        val chunked = streams.chunked(2)

        chunked.forEachIndexed { rowIdx, rowStreams ->
            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                rowStreams.forEachIndexed { colIdx, slot ->
                    val globalIdx = rowIdx * 2 + colIdx
                    MultiScreenCell(
                        slot = slot,
                        isActive = globalIdx == activeIndex,
                        context = context,
                        onClick = { onStreamSelect(globalIdx) },
                        onRemove = { onRemoveStream(globalIdx) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowStreams.size < 2) {
                    Box(modifier = Modifier.weight(1f).fillMaxSize().background(Color.Black))
                }
            }
        }
    }
}

@Composable
private fun MultiScreenCell(
    slot: StreamSlot,
    isActive: Boolean,
    context: Context,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val player = remember(slot.url) {
        buildAetherPlayer(context).also {
            it.setMediaItem(MediaItem.fromUri(slot.url))
            it.volume = if (isActive) 1f else 0f
            it.prepare()
            it.playWhenReady = true
        }
    }

    DisposableEffect(slot.url) {
        onDispose { player.release() }
    }

    player.volume = if (isActive) 1f else 0f

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = if (isActive) 2.dp else 0.dp,
                color = if (isActive) NeonIndigo else Color.Transparent,
                shape = RoundedCornerShape(4.dp),
            )
            .clickable(onClick = onClick),
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier.align(Alignment.TopEnd).size(36.dp),
        ) {
            Icon(Icons.Rounded.Close, contentDescription = "Cerrar stream", tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun MultiScreenToggleButton(
    isMultiScreen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = Icons.Rounded.GridView,
            contentDescription = if (isMultiScreen) "Pantalla única" else "Multi-pantalla",
            tint = if (isMultiScreen) NeonIndigo else Color.White,
        )
    }
}
