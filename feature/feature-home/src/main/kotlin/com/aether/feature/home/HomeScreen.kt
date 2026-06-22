package com.aether.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.aether.core.common.device.DeviceType
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.WatchHistoryEntity
import com.aether.core.ui.components.ShimmerChannelCard
import com.aether.core.ui.theme.DeepSpace
import com.aether.core.ui.theme.NeonIndigo
import com.aether.core.ui.theme.Void

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
        if (uiState.continueWatching.isNotEmpty()) {
            item {
                HomeSection(title = "Continuar viendo") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.continueWatching, key = { it.contentId }) { item ->
                            WatchHistoryCard(item = item, onClick = { onChannelClick(item.contentId) })
                        }
                    }
                }
            }
        }

        if (uiState.favoriteChannels.isNotEmpty() || uiState.isLoading) {
            item {
                HomeSection(title = "Canales favoritos") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (uiState.isLoading) {
                            items(5) { ShimmerChannelCard() }
                        } else {
                            items(uiState.favoriteChannels, key = { it.id }) { channel ->
                                ChannelCard(channel = channel, onClick = { onChannelClick(channel.id) })
                            }
                        }
                    }
                }
            }
        }

        if (uiState.recentChannels.isNotEmpty() || uiState.isLoading) {
            item {
                HomeSection(title = "Recientes") {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        if (uiState.isLoading) {
                            items(5) { ShimmerChannelCard() }
                        } else {
                            items(uiState.recentChannels, key = { it.id }) { channel ->
                                ChannelCard(channel = channel, onClick = { onChannelClick(channel.id) })
                            }
                        }
                    }
                }
            }
        }

        if (!uiState.isLoading && uiState.favoriteChannels.isEmpty() && uiState.recentChannels.isEmpty()) {
            item {
                EmptyHomeMessage(modifier = Modifier.fillMaxWidth().padding(32.dp))
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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp),
    ) {
        item {
            uiState.featuredChannel?.let { channel ->
                HeroChannelCard(
                    channel = channel,
                    onClick = { onChannelClick(channel.id) },
                )
            }
        }
        if (uiState.continueWatching.isNotEmpty()) {
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
        }
        if (uiState.favoriteChannels.isNotEmpty()) {
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
}

@Composable
private fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
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
    channel: ChannelEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(120.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(DeepSpace),
            contentAlignment = Alignment.Center,
        ) {
            if (channel.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = channel.logoUrl,
                    contentDescription = channel.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(64.dp),
                )
            } else {
                Text(
                    text = channel.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonIndigo,
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = channel.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun WatchHistoryCard(
    item: WatchHistoryEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(160.dp)
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(DeepSpace),
    ) {
        if (item.imageUrl.isNotBlank()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent, Void)),
                )
                .padding(8.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        val progress = if (item.durationMs > 0) item.positionMs.toFloat() / item.durationMs else 0f
        if (progress > 0f) {
            androidx.compose.material3.LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomStart),
                color = NeonIndigo,
                trackColor = androidx.compose.ui.graphics.Color.Transparent,
            )
        }
    }
}

@Composable
private fun HeroChannelCard(
    channel: ChannelEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 48.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(DeepSpace),
    ) {
        if (channel.logoUrl.isNotBlank()) {
            AsyncImage(
                model = channel.logoUrl,
                contentDescription = channel.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(160.dp)
                    .align(Alignment.Center),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(listOf(androidx.compose.ui.graphics.Color.Transparent, Void)),
                )
                .padding(24.dp),
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.headlineMedium,
            )
        }
    }
}

@Composable
private fun EmptyHomeMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Sin contenido todavía",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Añade un proveedor en Ajustes para empezar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
