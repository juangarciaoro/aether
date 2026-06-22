package com.aether.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LiveTv
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SearchScreen(
    onChannelClick: (String) -> Unit,
    onVodClick: (String) -> Unit,
    onSeriesClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::onQueryChange,
            placeholder = { Text("Buscar canales, películas, series...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )

        if (uiState.query.length >= 2 &&
            uiState.channels.isEmpty() &&
            uiState.vods.isEmpty() &&
            uiState.series.isEmpty()
        ) {
            Text(
                text = "Sin resultados para \"${uiState.query}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            if (uiState.channels.isNotEmpty()) {
                item { SearchSectionHeader("Canales") }
                items(uiState.channels, key = { it.id }) { channel ->
                    ListItem(
                        headlineContent = { Text(channel.name) },
                        supportingContent = { if (channel.tvgName.isNotBlank()) Text(channel.tvgName) },
                        leadingContent = { Icon(Icons.Rounded.LiveTv, contentDescription = null) },
                        modifier = Modifier.clickable { onChannelClick(channel.streamUrl) },
                    )
                }
            }
            if (uiState.vods.isNotEmpty()) {
                item { SearchSectionHeader("Películas") }
                items(uiState.vods, key = { it.id }) { vod ->
                    ListItem(
                        headlineContent = { Text(vod.name) },
                        supportingContent = { if (vod.year.isNotBlank()) Text(vod.year) },
                        leadingContent = { Icon(Icons.Rounded.Movie, contentDescription = null) },
                        modifier = Modifier.clickable { onVodClick(vod.id) },
                    )
                }
            }
            if (uiState.series.isNotEmpty()) {
                item { SearchSectionHeader("Series") }
                items(uiState.series, key = { it.id }) { series ->
                    ListItem(
                        headlineContent = { Text(series.name) },
                        supportingContent = { if (series.year.isNotBlank()) Text(series.year) },
                        leadingContent = { Icon(Icons.Rounded.Tv, contentDescription = null) },
                        modifier = Modifier.clickable { onSeriesClick(series.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
