package com.kosakata.inggris.data.repository

import com.kosakata.inggris.data.local.dao.LearningSessionDao
import com.kosakata.inggris.data.local.entity.LearningSessionEntity
import kotlinx.coroutines.flow.Flow

class LearningSessionRepository(private val dao: LearningSessionDao) {
    fun observeRecent(limit: Int = 10): Flow<List<LearningSessionEntity>> =
        dao.observeRecentSessions(limit)

    suspend fun save(
        category: String,
        totalWords: Int,
        correctAnswers: Int,
        wrongAnswers: Int,
        startedAt: Long,
        completedAt: Long = System.currentTimeMillis()
    ) {
        dao.insert(
            LearningSessionEntity(
                startedAt = startedAt,
                completedAt = completedAt,
                category = category,
                totalWords = totalWords,
                correctAnswers = correctAnswers,
                wrongAnswers = wrongAnswers
            )
        )
    }

    suspend fun reset() = dao.resetSessions()
}
