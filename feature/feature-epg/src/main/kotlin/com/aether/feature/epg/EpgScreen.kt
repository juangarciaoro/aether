package com.aether.feature.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aether.core.database.entity.EpgProgramEntity
import com.aether.core.ui.theme.DeepSpace
import com.aether.core.ui.theme.NeonIndigo
import java.text.SimpleDateFormat
import java.util.Locale

private val CHANNEL_COLUMN_WIDTH = 100.dp
private val CELL_HEIGHT = 64.dp
private val HOUR_WIDTH = 200.dp

@Composable
fun EpgScreen(
    onProgramClick: (EpgProgramEntity) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EpgViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val horizontalScroll = rememberScrollState()
    val scope = rememberCoroutineScope()

    Row(modifier = modifier.fillMaxSize()) {
        // Sticky channel column
        LazyColumn(
            modifier = Modifier.width(CHANNEL_COLUMN_WIDTH),
        ) {
            item { Box(modifier = Modifier.height(40.dp)) }
            items(uiState.channels) { channel ->
                Box(
                    modifier = Modifier
                        .width(CHANNEL_COLUMN_WIDTH)
                        .height(CELL_HEIGHT)
                        .background(DeepSpace)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = channel.name.take(12),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                    )
                }
            }
        }

        // Scrollable grid
        Box(modifier = Modifier.fillMaxSize().horizontalScroll(horizontalScroll)) {
            LazyColumn {
                // Time header
                item {
                    TimeHeader(startHour = uiState.startHour)
                }
                items(uiState.channels) { channel ->
                    EpgRow(
                        programs = uiState.programs[channel.tvgId] ?: emptyList(),
                        startHour = uiState.startHour,
                        onProgramClick = onProgramClick,
                    )
                }
            }
            // Now indicator
            NowIndicator(startHour = uiState.startHour)
        }
    }
}

@Composable
private fun TimeHeader(startHour: Long) {
    Row(modifier = Modifier.height(40.dp)) {
        for (i in 0..12) {
            Box(
                modifier = Modifier.width(HOUR_WIDTH),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = formatHour(startHour + i * 3600_000L),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun EpgRow(
    programs: List<EpgProgramEntity>,
    startHour: Long,
    onProgramClick: (EpgProgramEntity) -> Unit,
) {
    Row(modifier = Modifier.height(CELL_HEIGHT)) {
        programs.forEach { program ->
            val widthPx = ((program.endTime - program.startTime) / 3_600_000f * HOUR_WIDTH.value).dp
                .coerceAtLeast(4.dp)
            Box(
                modifier = Modifier
                    .width(widthPx)
                    .height(CELL_HEIGHT)
                    .background(DeepSpace)
                    .padding(1.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(8.dp),
            ) {
                Text(
                    text = program.title,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 2,
                )
            }
        }
    }
}

@Composable
private fun NowIndicator(startHour: Long) {
    val now = System.currentTimeMillis()
    val offsetHours = (now - startHour) / 3_600_000f
    val offsetDp = (offsetHours * HOUR_WIDTH.value).dp

    Box(
        modifier = Modifier
            .width(2.dp)
            .padding(start = offsetDp)
            .fillMaxSize()
            .background(NeonIndigo.copy(alpha = 0.8f)),
    )
}

private fun formatHour(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(timestamp)
}
