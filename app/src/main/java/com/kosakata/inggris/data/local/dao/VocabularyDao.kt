package com.kosakata.inggris.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(words: List<VocabularyWordEntity>)

    @Query("SELECT COUNT(*) FROM vocabulary_words")
    suspend fun countWords(): Int

    @Query("SELECT COUNT(*) FROM vocabulary_words")
    fun observeTotalWords(): Flow<Int>

    @Query("SELECT * FROM vocabulary_words WHERE isCore500 = 1 ORDER BY id LIMIT :limit")
    suspend fun getCoreWords(limit: Int): List<VocabularyWordEntity>

    @Query("SELECT * FROM vocabulary_words WHERE categories LIKE '%' || :category || '%' ORDER BY id LIMIT :limit")
    suspend fun getWordsByCategory(category: String, limit: Int): List<VocabularyWordEntity>

    @Query("""
        SELECT * FROM vocabulary_words
        WHERE id NOT IN (SELECT wordId FROM user_word_progress WHERE status != 'NEW')
        AND (
            (:category = '500 Kata Paling Berguna' AND isCore500 = 1)
            OR categories LIKE '%' || :category || '%'
        )
        ORDER BY id LIMIT :limit
    """)
    suspend fun getNewWordsByCategory(category: String, limit: Int): List<VocabularyWordEntity>

    @Query("SELECT * FROM vocabulary_words WHERE id IN (:ids)")
    suspend fun getWordsByIds(ids: List<Int>): List<VocabularyWordEntity>

    @Query("SELECT * FROM vocabulary_words ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomWords(limit: Int): List<VocabularyWordEntity>
}
