/*
 * Tujuan: Menjadi facade data utama bagi seluruh screen aplikasi.
 * Caller: Screen Compose dan VocabViewModel.
 * Dependensi: Repository vocabulary/progress/session, Room, dan UserPreferences.
 * Main Functions: statistik, sesi harian, library filter, progres kategori, save/reset.
 * Side Effects: Membaca/menulis Room, DataStore, dan assets saat seed.
 */
package com.kosakata.inggris.data.repository

import android.content.Context
import com.kosakata.inggris.data.local.AppDatabase
import com.kosakata.inggris.data.local.entity.LearningSessionEntity
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.local.model.CategoryProgress
import com.kosakata.inggris.data.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class VocabRepository(
    database: AppDatabase,
    val preferences: UserPreferences,
    context: Context
) {
    private val vocabulary = VocabularyRepository(database, context.applicationContext)
    private val progress = UserProgressRepository(database.userProgressDao())
    private val sessions = LearningSessionRepository(database.learningSessionDao())

    suspend fun seedIfNeeded() = vocabulary.seedIfNeeded()

    fun observeTotalWords(): Flow<Int> = vocabulary.observeTotalWords()
    fun observeMasteredCount(): Flow<Int> = progress.observeMasteredCount()
    fun observeLearningCount(): Flow<Int> = progress.observeLearningCount()
    fun observeLearnedCount(): Flow<Int> = progress.observeLearnedCount()
    fun observeDueReviewCount(): Flow<Int> =
        progress.observeDueReviewCount(System.currentTimeMillis())
    fun observeBookmarkedCount(): Flow<Int> = progress.observeBookmarkedCount()
    fun observeTomorrowReviewCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return progress.observeReviewCountBetween(start, calendar.timeInMillis)
    }

    fun observeRecentSessions(limit: Int = 10): Flow<List<LearningSessionEntity>> =
        sessions.observeRecent(limit)

    suspend fun getTodayWords(target: Int, category: String): List<VocabularyWordEntity> {
        seedIfNeeded()
        val dueIds = progress.getDueReviewWordIds(System.currentTimeMillis(), target)
        val dueWords = vocabulary.getWordsByIds(dueIds)
        val remaining = (target - dueWords.size).coerceAtLeast(0)
        val newWords = if (remaining > 0) {
            vocabulary.getNewWordsByCategory(category, remaining)
        } else {
            emptyList()
        }
        return (dueWords + newWords).distinctBy { it.id }.take(target)
    }

    suspend fun getCategoryWords(category: String, limit: Int = 100): List<VocabularyWordEntity> {
        seedIfNeeded()
        return if (category == "500 Kata Paling Berguna") {
            vocabulary.getCoreWords(limit)
        } else {
            vocabulary.getWordsByCategory(category, limit)
        }
    }

    suspend fun getCategoryProgress(category: String): CategoryProgress {
        seedIfNeeded()
        return vocabulary.getCategoryProgress(category)
    }

    suspend fun getReviewWords(limit: Int = 50): List<VocabularyWordEntity> {
        seedIfNeeded()
        return vocabulary.getWordsByIds(
            progress.getDueReviewWordIds(System.currentTimeMillis(), limit)
        )
    }

    suspend fun getBookmarkedWords(limit: Int = 100): List<VocabularyWordEntity> =
        vocabulary.getWordsByIds(progress.getBookmarkedIds(limit))

    suspend fun getMasteredWords(limit: Int = 100): List<VocabularyWordEntity> =
        vocabulary.getWordsByIds(progress.getMasteredIds(limit))

    suspend fun getDifficultWords(limit: Int = 50): List<VocabularyWordEntity> =
        vocabulary.getWordsByIds(progress.getDifficultWordIds(limit))

    suspend fun getRandomWords(limit: Int): List<VocabularyWordEntity> {
        seedIfNeeded()
        return vocabulary.getRandomWords(limit)
    }

    suspend fun markSeen(wordId: Int, knewIt: Boolean) =
        progress.recordAnswer(wordId, knewIt)

    suspend fun updateQuizProgress(wordId: Int, isCorrect: Boolean) =
        progress.recordAnswer(wordId, isCorrect)

    suspend fun toggleBookmark(wordId: Int) = progress.toggleBookmark(wordId)

    suspend fun saveSession(
        category: String,
        totalWords: Int,
        correct: Int = 0,
        wrong: Int = 0,
        startedAt: Long = System.currentTimeMillis()
    ) {
        sessions.save(category, totalWords, correct, wrong, startedAt)
        preferences.recordStudyDay()
    }

    suspend fun resetProgress() {
        progress.reset()
        sessions.reset()
        preferences.resetLearningStats()
    }
}
