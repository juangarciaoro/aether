package com.aether.feature.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Satellite
import androidx.compose.material.icons.rounded.Subtitles
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    onManageProviders: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        SettingsSectionHeader(title = "Fuentes IPTV")
        ListItem(
            headlineContent = { Text("Gestionar proveedores") },
            supportingContent = { Text("Añadir, editar o eliminar fuentes") },
            leadingContent = { Icon(Icons.Rounded.Satellite, contentDescription = null) },
        )
        HorizontalDivider()

        SettingsSectionHeader(title = "Reproductor")
        ListItem(
            headlineContent = { Text("PiP automático al salir") },
            leadingContent = { Icon(Icons.Rounded.PlayCircle, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = uiState.pipAutoEnter,
                    onCheckedChange = viewModel::setPipAutoEnter,
                )
            },
        )
        ListItem(
            headlineContent = { Text("Continuar donde lo dejé") },
            leadingContent = { Icon(Icons.Rounded.PlayCircle, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = uiState.resumePlayback,
                    onCheckedChange = viewModel::setResumePlayback,
                )
            },
        )
        HorizontalDivider()

        SettingsSectionHeader(title = "Subtítulos")
        ListItem(
            headlineContent = { Text("Subtítulos activados") },
            leadingContent = { Icon(Icons.Rounded.Subtitles, contentDescription = null) },
            trailingContent = {
                Switch(
                    checked = uiState.subtitleEnabled,
                    onCheckedChange = viewModel::setSubtitleEnabled,
                )
            },
        )
        HorizontalDivider()

        SettingsSectionHeader(title = "EPG")
        ListItem(
            headlineContent = { Text("Días de EPG almacenados") },
            supportingContent = { Text("${uiState.epgDaysToKeep} días") },
            leadingContent = { Icon(Icons.Rounded.Tv, contentDescription = null) },
        )
        HorizontalDivider()

        SettingsSectionHeader(title = "Apariencia")
        ListItem(
            headlineContent = { Text("Tema Chromatic Void") },
            supportingContent = { Text("Oscuro con acento Neon Indigo") },
            leadingContent = { Icon(Icons.Rounded.Palette, contentDescription = null) },
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
