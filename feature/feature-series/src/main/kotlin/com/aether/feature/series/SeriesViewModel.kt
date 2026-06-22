package com.aether.feature.series

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.SeriesDao
import com.aether.core.database.entity.SeriesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SeriesUiState(
    val series: List<SeriesEntity> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class SeriesViewModel @Inject constructor(
    private val seriesDao: SeriesDao,
) : ViewModel() {

    val uiState = seriesDao.observeByProvider(1L)
        .map { SeriesUiState(series = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SeriesUiState(isLoading = true),
        )
}
