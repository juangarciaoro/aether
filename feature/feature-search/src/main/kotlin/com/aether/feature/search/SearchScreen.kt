package com.aether.feature.search

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
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

        LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
            if (uiState.channels.isNotEmpty()) {
                item { SearchSectionHeader("Canales") }
                items(uiState.channels) { channel ->
                    ListItem(
                        headlineContent = { Text(channel.name) },
                        supportingContent = { Text(channel.tvgName) },
                    )
                }
            }
            if (uiState.vods.isNotEmpty()) {
                item { SearchSectionHeader("Películas") }
                items(uiState.vods) { vod ->
                    ListItem(
                        headlineContent = { Text(vod.name) },
                        supportingContent = { Text(vod.year) },
                    )
                }
            }
            if (uiState.series.isNotEmpty()) {
                item { SearchSectionHeader("Series") }
                items(uiState.series) { series ->
                    ListItem(
                        headlineContent = { Text(series.name) },
                        supportingContent = { Text(series.year) },
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
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
