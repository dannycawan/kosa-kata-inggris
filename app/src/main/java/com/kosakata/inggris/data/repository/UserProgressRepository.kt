/*
 * Tujuan: Menerapkan aturan progres, review, mastery, dan bookmark per kata.
 * Caller: VocabRepository.
 * Dependensi: UserProgressDao dan ReviewScheduler.
 * Main Functions: recordAnswer, toggleBookmark, statistik, query daftar kata, reset.
 * Side Effects: Membaca dan menulis tabel user_word_progress.
 */
package com.kosakata.inggris.data.repository

import com.kosakata.inggris.data.local.dao.UserProgressDao
import com.kosakata.inggris.data.local.entity.UserWordProgressEntity
import com.kosakata.inggris.domain.review.ReviewScheduler
import kotlinx.coroutines.flow.Flow

class UserProgressRepository(private val dao: UserProgressDao) {
    fun observeMasteredCount(): Flow<Int> = dao.observeMasteredCount()
    fun observeLearningCount(): Flow<Int> = dao.observeLearningCount()
    fun observeLearnedCount(): Flow<Int> = dao.observeLearnedCount()
    fun observeDueReviewCount(now: Long): Flow<Int> = dao.observeDueReviewCount(now)
    fun observeBookmarkedCount(): Flow<Int> = dao.observeBookmarkedCount()
    fun observeReviewCountBetween(start: Long, end: Long): Flow<Int> =
        dao.observeReviewCountBetween(start, end)

    suspend fun getDueReviewWordIds(now: Long, limit: Int): List<Int> =
        dao.getDueReviewWordIds(now, limit)

    suspend fun getBookmarkedIds(limit: Int): List<Int> = dao.getBookmarkedIds(limit)
    suspend fun getMasteredIds(limit: Int): List<Int> = dao.getMasteredIds(limit)
    suspend fun getDifficultWordIds(limit: Int): List<Int> = dao.getDifficultWordIds(limit)

    suspend fun recordAnswer(wordId: Int, isCorrect: Boolean, now: Long = System.currentTimeMillis()) {
        val current = dao.getProgress(wordId) ?: UserWordProgressEntity(wordId = wordId)
        val updated = if (isCorrect) {
            val correctCount = (current.correctCount + 1).coerceAtMost(5)
            current.copy(
                status = if (correctCount >= 5) "MASTERED" else "REVIEW",
                correctCount = correctCount,
                lastReviewedAt = now,
                nextReviewAt = ReviewScheduler.nextReviewAt(correctCount, now)
            )
        } else {
            current.copy(
                status = "LEARNING",
                correctCount = 0,
                wrongCount = current.wrongCount + 1,
                lastReviewedAt = now,
                nextReviewAt = ReviewScheduler.tomorrow(now)
            )
        }
        dao.upsert(updated)
    }

    suspend fun toggleBookmark(wordId: Int) {
        val current = dao.getProgress(wordId) ?: UserWordProgressEntity(wordId = wordId)
        dao.upsert(current.copy(isBookmarked = !current.isBookmarked))
    }

    suspend fun reset() = dao.resetProgress()
}
