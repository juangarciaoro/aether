package com.aether.feature.vod

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.VodDao
import com.aether.core.database.entity.VodEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class VodUiState(
    val vods: List<VodEntity> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class VodViewModel @Inject constructor(
    private val vodDao: VodDao,
) : ViewModel() {

    val uiState = vodDao.observeByProvider(1L)
        .map { VodUiState(vods = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VodUiState(isLoading = true),
        )
}
