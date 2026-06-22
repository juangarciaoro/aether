package com.aether.feature.epg

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aether.core.database.dao.ChannelDao
import com.aether.core.database.dao.EpgDao
import com.aether.core.database.entity.ChannelEntity
import com.aether.core.database.entity.EpgProgramEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

data class EpgUiState(
    val channels: List<ChannelEntity> = emptyList(),
    val programs: Map<String, List<EpgProgramEntity>> = emptyMap(),
    val startHour: Long = 0L,
    val isLoading: Boolean = false,
)

@HiltViewModel
class EpgViewModel @Inject constructor(
    private val channelDao: ChannelDao,
    private val epgDao: EpgDao,
) : ViewModel() {

    private val startHour: Long = run {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.timeInMillis
    }

    private val endHour: Long = startHour + 24 * 3_600_000L

    val uiState = combine(
        channelDao.observeByProvider(1L),
        epgDao.observeAllForTimeRange(startHour, endHour),
    ) { channels, programs ->
        val top50 = channels.take(50)
        val programsByChannel = programs.groupBy { it.channelTvgId }
        EpgUiState(
            channels = top50,
            programs = programsByChannel,
            startHour = startHour,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = EpgUiState(isLoading = true, startHour = startHour),
    )
}
