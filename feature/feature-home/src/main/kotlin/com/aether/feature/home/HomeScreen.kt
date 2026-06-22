package com.aether.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aether.core.common.device.DeviceType
import com.aether.core.ui.components.ErrorScreen
import com.aether.core.ui.components.ShimmerChannelCard

@Composable
fun HomeScreen(
    deviceType: DeviceType,
    onChannelClick: (String) -> Unit,
    onVodClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    when (deviceType) {
        DeviceType.TV -> HomeTvLayout(
            uiState = uiState,
            onChannelClick = onChannelClick,
            modifier = modifier,
        )
        else -> HomePhoneLayout(
            uiState = uiState,
            onChannelClick = onChannelClick,
            onVodClick = onVodClick,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomePhoneLayout(
    uiState: HomeUiState,
    onChannelClick: (String) -> Unit,
    onVodClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            HomeSection(title = "Continuar viendo") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (uiState.continueWatching.isEmpty() && uiState.isLoading) {
                        items(5) { ShimmerChannelCard() }
                    } else {
                        items(uiState.continueWatching, key = { it.contentId }) { item ->
                            WatchHistoryCard(item = item, onClick = { onChannelClick(item.contentId) })
                        }
                    }
                }
            }
        }
        item {
            HomeSection(title = "Canales favoritos") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.favoriteChannels, key = { it.id }) { channel ->
                        ChannelCard(channel = channel, onClick = { onChannelClick(channel.id) })
                    }
                }
            }
        }
        item {
            HomeSection(title = "Recientes") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.recentChannels, key = { it.id }) { channel ->
                        ChannelCard(channel = channel, onClick = { onChannelClick(channel.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTvLayout(
    uiState: HomeUiState,
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // TV layout: Hero + horizontal rows
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        item {
            // Hero card for featured channel
            uiState.featuredChannel?.let { channel ->
                HeroChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel.id) },
                )
            }
        }
        item {
            HomeSection(title = "Continuar viendo") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(uiState.continueWatching, key = { it.contentId }) { item ->
                        WatchHistoryCard(item = item, onClick = { onChannelClick(item.contentId) })
                    }
                }
            }
        }
        item {
            HomeSection(title = "Mis favoritos") {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(uiState.favoriteChannels, key = { it.id }) { channel ->
                        ChannelCard(channel = channel, onClick = { onChannelClick(channel.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        content()
    }
}

@Composable
private fun ChannelCard(
    channel: com.aether.core.database.entity.ChannelEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // TODO: implement with ChromaticFocusCard
    ShimmerChannelCard(modifier = modifier)
}

@Composable
private fun WatchHistoryCard(
    item: com.aether.core.database.entity.WatchHistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ShimmerChannelCard(modifier = modifier)
}

@Composable
private fun HeroChannelCard(
    channel: com.aether.core.database.entity.ChannelEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ShimmerChannelCard(modifier = modifier)
}
