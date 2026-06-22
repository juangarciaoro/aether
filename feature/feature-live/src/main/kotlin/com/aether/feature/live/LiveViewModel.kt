package com.aether.feature.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.EpgDao
import com.aether.core.database.entity.CategoryEntity
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.EpgProgramEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class LiveUiState(
    val channels: List<ChannelEntity> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val currentPrograms: Map<String, EpgProgramEntity> = emptyMap(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val epgDao: EpgDao,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = combine(
        _searchQuery,
        _selectedCategory,
    ) { query, category ->
        Pair(query, category)
    }.flatMapLatest { (query, category) ->
        if (query.isBlank()) {
            if (category != null) channelDao.observeByCategory(category)
            else channelDao.observeByProvider(1L)
        } else {
            channelDao.search(query)
        }
    }.combine(_searchQuery) { channels, query ->
        LiveUiState(
            channels = channels,
            searchQuery = query,
            selectedCategory = _selectedCategory.value,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LiveUiState(isLoading = true),
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.update { query }
    }

    fun onCategorySelected(categoryId: String?) {
        _selectedCategory.update { categoryId }
    }
}
