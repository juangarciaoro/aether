package com.aether.feature.series

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.ProviderDao
import com.aether.core.database.dao.SeriesDao
import com.aether.core.database.entity.ProviderEntity
import com.aether.core.database.entity.SeriesEntity
import com.aether.data.xtream.XtreamRepository
import com.aether.data.xtream.model.XtreamEpisode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EpisodeItem(
    val episodeNum: Int,
    val title: String,
    val url: String,
    val plot: String = "",
    val imageUrl: String = "",
    val durationSecs: Int = 0,
)

data class SeriesDetailUiState(
    val series: SeriesEntity? = null,
    val seasons: List<Int> = emptyList(),
    val selectedSeason: Int = 1,
    val episodesForSeason: List<EpisodeItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class SeriesDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val seriesDao: SeriesDao,
    private val providerDao: ProviderDao,
    private val xtreamRepository: XtreamRepository,
) : ViewModel() {

    private val seriesId: String = checkNotNull(savedStateHandle["seriesId"])

    private val _uiState = MutableStateFlow(SeriesDetailUiState())
    val uiState = _uiState.asStateFlow()

    private var allEpisodes: Map<String, List<XtreamEpisode>> = emptyMap()

    init {
        viewModelScope.launch {
            val series = seriesDao.getById(seriesId)
            _uiState.update { it.copy(series = series) }
            if (series != null) {
                loadSeriesInfo(series)
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun loadSeriesInfo(series: SeriesEntity) {
        val providers = providerDao.observeAll().first()
        val provider = providers.firstOrNull { it.isActive } ?: return

        runCatching {
            xtreamRepository.getSeriesInfo(
                baseUrl = provider.url,
                username = provider.username,
                password = provider.password,
                seriesId = series.seriesId,
            )
        }.onSuccess { info ->
            allEpisodes = info.episodes
            val seasons = info.seasons
                .map { it.seasonNumber }
                .distinct()
                .sorted()
                .ifEmpty {
                    info.episodes.keys.mapNotNull { it.toIntOrNull() }.distinct().sorted()
                }
            val firstSeason = seasons.firstOrNull() ?: 1
            _uiState.update {
                it.copy(
                    seasons = seasons,
                    selectedSeason = firstSeason,
                    episodesForSeason = buildEpisodeItems(allEpisodes, firstSeason, provider),
                    isLoading = false,
                )
            }
        }.onFailure { e ->
            _uiState.update { it.copy(isLoading = false, error = e.message) }
        }
    }

    fun selectSeason(season: Int) {
        viewModelScope.launch {
            val providers = providerDao.observeAll().first()
            val provider = providers.firstOrNull { it.isActive } ?: return@launch
            _uiState.update {
                it.copy(
                    selectedSeason = season,
                    episodesForSeason = buildEpisodeItems(allEpisodes, season, provider),
                )
            }
        }
    }

    private fun buildEpisodeItems(
        episodes: Map<String, List<XtreamEpisode>>,
        season: Int,
        provider: ProviderEntity,
    ): List<EpisodeItem> =
        episodes[season.toString()]
            ?.sortedBy { it.episodeNum }
            ?.map { ep ->
                EpisodeItem(
                    episodeNum = ep.episodeNum,
                    title = ep.title.ifBlank { "Episodio ${ep.episodeNum}" },
                    url = xtreamRepository.buildSeriesUrl(
                        provider.url, provider.username, provider.password,
                        ep.id, ep.containerExtension,
                    ),
                    plot = ep.info?.plot ?: "",
                    imageUrl = ep.info?.movieImage ?: "",
                    durationSecs = ep.info?.durationSecs ?: 0,
                )
            } ?: emptyList()
}
