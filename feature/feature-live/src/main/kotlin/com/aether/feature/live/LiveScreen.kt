package com.aether.feature.live

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.ui.components.LiveBadge
import com.aether.core.ui.components.shimmerEffect

@Composable
fun LiveScreen(
    onChannelClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LiveViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            placeholder = { Text("Buscar canal...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(bottom = 8.dp),
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedCategory == null,
                    onClick = { viewModel.onCategorySelected(null) },
                    label = { Text("Todos") },
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
            items(uiState.categories) { category ->
                FilterChip(
                    selected = uiState.selectedCategory == category.id,
                    onClick = { viewModel.onCategorySelected(category.id) },
                    label = { Text(category.name) },
                    modifier = Modifier.padding(end = 8.dp),
                )
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                items(8) {
                    ShimmerChannelListItem()
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            } else {
                items(uiState.channels, key = { it.id }) { channel ->
                    ChannelListItem(
                        channel = channel,
                        currentProgram = uiState.currentPrograms[channel.tvgId],
                        onClick = { onChannelClick(channel.id) },
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun ShimmerChannelListItem(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .shimmerEffect(),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(16.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(12.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(4.dp))
                    .shimmerEffect(),
            )
        }
    }
}

@Composable
private fun ChannelListItem(
    channel: ChannelEntity,
    currentProgram: com.aether.core.database.entity.EpgProgramEntity?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        coil3.compose.AsyncImage(
            model = channel.logoUrl,
            contentDescription = channel.name,
            modifier = Modifier.size(48.dp),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit,
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )
                LiveBadge()
            }
            if (currentProgram != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentProgram.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
                val now = System.currentTimeMillis()
                val progress = if (currentProgram.endTime > currentProgram.startTime) {
                    ((now - currentProgram.startTime).toFloat() / (currentProgram.endTime - currentProgram.startTime)).coerceIn(0f, 1f)
                } else 0f
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
