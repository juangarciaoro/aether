package com.aether.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.aether.core.database.entity.EpgProgramEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EpgDao {

    @Upsert
    suspend fun upsertAll(programs: List<EpgProgramEntity>)

    @Query("SELECT * FROM epg_programs WHERE channelTvgId = :tvgId AND endTime >= :now ORDER BY startTime ASC LIMIT 5")
    fun observeUpcoming(tvgId: String, now: Long): Flow<List<EpgProgramEntity>>

    @Query("SELECT * FROM epg_programs WHERE channelTvgId = :tvgId AND startTime <= :now AND endTime >= :now LIMIT 1")
    fun observeCurrentProgram(tvgId: String, now: Long): Flow<EpgProgramEntity?>

    @Query("SELECT * FROM epg_programs WHERE channelTvgId = :tvgId AND startTime >= :from AND startTime < :to ORDER BY startTime ASC")
    fun observeForTimeRange(tvgId: String, from: Long, to: Long): Flow<List<EpgProgramEntity>>

    @Query("SELECT * FROM epg_programs WHERE startTime < :to AND endTime > :from ORDER BY channelTvgId, startTime ASC")
    fun observeAllForTimeRange(from: Long, to: Long): Flow<List<EpgProgramEntity>>

    @Query("DELETE FROM epg_programs WHERE endTime < :before")
    suspend fun deleteOldPrograms(before: Long)
}
