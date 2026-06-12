package com.kosakata.inggris.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kosakata.inggris.data.local.entity.LearningSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LearningSessionDao {
    @Insert
    suspend fun insert(session: LearningSessionEntity): Long

    @Query("SELECT * FROM learning_sessions ORDER BY startedAt DESC LIMIT :limit")
    fun observeRecentSessions(limit: Int = 10): Flow<List<LearningSessionEntity>>

    @Query("DELETE FROM learning_sessions")
    suspend fun resetSessions()
}
