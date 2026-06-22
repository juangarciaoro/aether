package com.aether.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aether.core.ui.components.AetherButton
import com.aether.core.ui.theme.Error

enum class ProviderType { XTREAM, M3U }

@Composable
fun ProviderSetupScreen(
    onProviderAdded: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedType by remember { mutableStateOf(ProviderType.XTREAM) }
    var serverUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var m3uUrl by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Añadir proveedor",
            style = MaterialTheme.typography.headlineMedium,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedType == ProviderType.XTREAM,
                onClick = { selectedType = ProviderType.XTREAM },
                label = { Text("Xtream Codes") },
            )
            FilterChip(
                selected = selectedType == ProviderType.M3U,
                onClick = { selectedType = ProviderType.M3U },
                label = { Text("Lista M3U") },
            )
        }

        when (selectedType) {
            ProviderType.XTREAM -> XtreamForm(
                serverUrl = serverUrl,
                username = username,
                password = password,
                onServerUrlChange = { serverUrl = it },
                onUsernameChange = { username = it },
                onPasswordChange = { password = it },
            )
            ProviderType.M3U -> M3uForm(
                url = m3uUrl,
                onUrlChange = { m3uUrl = it },
            )
        }

        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                style = MaterialTheme.typography.bodySmall,
                color = Error,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AetherButton(
            onClick = {
                when (selectedType) {
                    ProviderType.XTREAM -> viewModel.connectXtream(serverUrl, username, password, onProviderAdded)
                    ProviderType.M3U -> viewModel.connectM3u(m3uUrl, onProviderAdded)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isConnecting && when (selectedType) {
                ProviderType.XTREAM -> serverUrl.isNotBlank() && username.isNotBlank()
                ProviderType.M3U -> m3uUrl.isNotBlank()
            },
        ) {
            if (uiState.isConnecting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Conectar y sincronizar")
            }
        }
    }
}

@Composable
private fun XtreamForm(
    serverUrl: String,
    username: String,
    password: String,
    onServerUrlChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = serverUrl,
        onValueChange = onServerUrlChange,
        label = { Text("URL del servidor") },
        placeholder = { Text("http://servidor.com:8080") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
    OutlinedTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = { Text("Usuario") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Contraseña") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
private fun M3uForm(
    url: String,
    onUrlChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = url,
        onValueChange = onUrlChange,
        label = { Text("URL de la lista M3U") },
        placeholder = { Text("http://proveedor.com/lista.m3u") },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Done,
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}
