package com.aether.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.SeriesDao
import com.aether.core.database.dao.VodDao
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.SeriesEntity
import com.aether.core.database.entity.VodEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val channels: List<ChannelEntity> = emptyList(),
    val vods: List<VodEntity> = emptyList(),
    val series: List<SeriesEntity> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val vodDao: VodDao,
    private val seriesDao: SeriesDao,
) : ViewModel() {

    private val _query = MutableStateFlow("")

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState = _query
        .debounce(300)
        .flatMapLatest { query ->
            if (query.length < 2) {
                flowOf(SearchUiState(query = query))
            } else {
                combine(
                    channelDao.search(query),
                    vodDao.search(query),
                    seriesDao.search(query),
                ) { channels, vods, series ->
                    SearchUiState(
                        query = query,
                        channels = channels,
                        vods = vods,
                        series = series,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchUiState(),
        )

    fun onQueryChange(query: String) {
        _query.update { query }
    }
}
