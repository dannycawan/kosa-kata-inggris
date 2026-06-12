/*
 * Tujuan: Menyediakan query Room untuk status belajar, bookmark, dan antrean review.
 * Caller: UserProgressRepository dan VocabRepository.
 * Dependensi: Room dan UserWordProgressEntity.
 * Main Functions: upsert, statistik Flow, query due/bookmark/mastered/difficult, resetProgress.
 * Side Effects: Membaca dan menulis tabel user_word_progress.
 */
package com.kosakata.inggris.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kosakata.inggris.data.local.entity.UserWordProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserWordProgressEntity)

    @Query("SELECT * FROM user_word_progress WHERE wordId = :wordId LIMIT 1")
    suspend fun getProgress(wordId: Int): UserWordProgressEntity?

    @Query(
        """
        SELECT wordId FROM user_word_progress
        WHERE nextReviewAt IS NOT NULL
          AND nextReviewAt <= :now
          AND status != 'MASTERED'
        ORDER BY nextReviewAt ASC
        LIMIT :limit
        """
    )
    suspend fun getDueReviewWordIds(now: Long, limit: Int): List<Int>

    @Query("SELECT COUNT(*) FROM user_word_progress WHERE status = 'MASTERED'")
    fun observeMasteredCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_word_progress WHERE status IN ('LEARNING','REVIEW')")
    fun observeLearningCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_word_progress WHERE status != 'NEW'")
    fun observeLearnedCount(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM user_word_progress
        WHERE nextReviewAt IS NOT NULL
          AND nextReviewAt <= :now
          AND status != 'MASTERED'
        """
    )
    fun observeDueReviewCount(now: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM user_word_progress WHERE isBookmarked = 1")
    fun observeBookmarkedCount(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM user_word_progress
        WHERE nextReviewAt >= :start
          AND nextReviewAt < :end
          AND status != 'MASTERED'
        """
    )
    fun observeReviewCountBetween(start: Long, end: Long): Flow<Int>

    @Query("SELECT wordId FROM user_word_progress WHERE isBookmarked = 1 ORDER BY wordId LIMIT :limit")
    suspend fun getBookmarkedIds(limit: Int): List<Int>

    @Query("SELECT wordId FROM user_word_progress WHERE status = 'MASTERED' ORDER BY wordId LIMIT :limit")
    suspend fun getMasteredIds(limit: Int): List<Int>

    @Query("SELECT wordId FROM user_word_progress WHERE wrongCount > 0 ORDER BY wrongCount DESC LIMIT :limit")
    suspend fun getDifficultWordIds(limit: Int): List<Int>

    @Query("DELETE FROM user_word_progress")
    suspend fun resetProgress()
}
