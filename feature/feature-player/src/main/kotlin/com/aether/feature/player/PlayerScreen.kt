package com.aether.feature.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.aether.core.player.buildAetherPlayer

@Composable
fun PlayerScreen(
    streamUrl: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val player = remember {
        buildAetherPlayer(context).also { viewModel.setPlayer(it) }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
            viewModel.clearPlayer()
        }
    }

    LaunchedEffect(streamUrl) {
        viewModel.loadStream(streamUrl)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        if (uiState.isMultiScreen && uiState.multiScreenStreams.isNotEmpty()) {
            MultiScreenLayout(
                streams = uiState.multiScreenStreams,
                activeIndex = uiState.activeMultiScreenIndex,
                onStreamSelect = viewModel::selectMultiScreenStream,
                onRemoveStream = viewModel::removeMultiScreenStream,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        this.player = player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        AnimatedVisibility(
            visible = uiState.showControls || uiState.isMultiScreen,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            PlayerControls(
                uiState = uiState,
                onPlayPause = viewModel::togglePlayPause,
                onSeekBack = { viewModel.seekRelative(-10_000) },
                onSeekForward = { viewModel.seekRelative(10_000) },
                onBack = onBack,
                onToggleMultiScreen = { viewModel.toggleMultiScreen(streamUrl) },
            )
        }
    }
}
