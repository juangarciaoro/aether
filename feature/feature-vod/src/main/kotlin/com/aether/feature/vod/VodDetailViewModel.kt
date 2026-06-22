package com.aether.feature.vod

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.VodDao
import com.aether.core.database.entity.VodEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VodDetailUiState(
    val vod: VodEntity? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class VodDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vodDao: VodDao,
) : ViewModel() {

    private val vodId: String = checkNotNull(savedStateHandle["vodId"])

    private val _uiState = MutableStateFlow(VodDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val vod = vodDao.getById(vodId)
            _uiState.update { it.copy(vod = vod, isLoading = false) }
        }
    }
}
