package com.aether.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.FavoriteDao
import com.aether.core.database.dao.WatchHistoryDao
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.WatchHistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val featuredChannel: ChannelEntity? = null,
    val continueWatching: List<WatchHistoryEntity> = emptyList(),
    val favoriteChannels: List<ChannelEntity> = emptyList(),
    val recentChannels: List<ChannelEntity> = emptyList(),
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val watchHistoryDao: WatchHistoryDao,
    private val favoriteDao: FavoriteDao,
    private val channelDao: ChannelDao,
) : ViewModel() {

    val uiState = combine(
        watchHistoryDao.observeRecent(),
        favoriteDao.observeAll(),
    ) { history, favorites ->
        HomeUiState(
            isLoading = false,
            continueWatching = history.take(10),
            favoriteChannels = emptyList(),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(isLoading = true),
    )
}
