package com.aether.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.common.result.AetherResult
import com.aether.core.database.dao.ProviderDao
import com.aether.core.database.entity.ProviderEntity
import com.aether.data.xtream.XtreamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isConnecting: Boolean = false,
    val connectionSuccess: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val xtreamRepository: XtreamRepository,
    private val providerDao: ProviderDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState = _uiState.asStateFlow()

    fun connectXtream(
        serverUrl: String,
        username: String,
        password: String,
        onSuccess: () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, error = null) }
            val result = xtreamRepository.authenticate(serverUrl.trimEnd('/'), username, password)
            when (result) {
                is AetherResult.Success -> {
                    val providerId = providerDao.upsert(
                        ProviderEntity(
                            name = serverUrl.removePrefix("http://").removePrefix("https://").substringBefore("/"),
                            type = "xtream",
                            url = serverUrl.trimEnd('/'),
                            username = username,
                            password = password,
                        )
                    )
                    xtreamRepository.syncLiveCategories(providerId, serverUrl.trimEnd('/'), username, password)
                    xtreamRepository.syncLiveStreams(providerId, serverUrl.trimEnd('/'), username, password)
                    _uiState.update { it.copy(isConnecting = false, connectionSuccess = true) }
                    onSuccess()
                }
                is AetherResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isConnecting = false,
                            error = result.exception.message ?: "Error de conexión",
                        )
                    }
                }
                is AetherResult.Loading -> Unit
            }
        }
    }

    fun connectM3u(url: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true, error = null) }
            runCatching {
                providerDao.upsert(
                    ProviderEntity(
                        name = url.substringAfterLast("/").substringBefore("?").ifBlank { "Lista M3U" },
                        type = "m3u",
                        url = url,
                    )
                )
            }.fold(
                onSuccess = {
                    _uiState.update { it.copy(isConnecting = false, connectionSuccess = true) }
                    onSuccess()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isConnecting = false, error = e.message ?: "Error") }
                },
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
