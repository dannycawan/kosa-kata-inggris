/*
 * Tujuan: Mengelola seeding dan operasi baca katalog vocabulary.
 * Caller: VocabRepository.
 * Dependensi: AppDatabase, VocabularyDao, VocabularySeeder, coroutine IO.
 * Main Functions: seedIfNeeded, query vocabulary, getCategoryProgress.
 * Side Effects: Membaca assets, membaca/menulis Room saat seeding.
 */
package com.kosakata.inggris.data.repository

import android.content.Context
import android.util.Log
import com.kosakata.inggris.data.local.AppDatabase
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.local.model.CategoryProgress
import com.kosakata.inggris.data.seed.VocabularySeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class VocabularyRepository(
    private val database: AppDatabase,
    private val context: Context
) {
    private val dao = database.vocabularyDao()
    private val seedMutex = Mutex()

    suspend fun seedIfNeeded() {
        seedMutex.withLock {
            withContext(Dispatchers.IO) {
                runCatching {
                    VocabularySeeder.seedIfNeeded(context, database)
                }.onFailure { error ->
                    Log.e(TAG, "Gagal mengimpor vocabulary dari assets", error)
                }
            }
        }
    }

    fun observeTotalWords(): Flow<Int> = dao.observeTotalWords()

    suspend fun getCoreWords(limit: Int): List<VocabularyWordEntity> = dao.getCoreWords(limit)

    suspend fun getWordsByCategory(category: String, limit: Int): List<VocabularyWordEntity> =
        dao.getWordsByCategory(category, limit)

    suspend fun getNewWordsByCategory(category: String, limit: Int): List<VocabularyWordEntity> =
        dao.getNewWordsByCategory(category, limit)

    suspend fun getWordsByIds(ids: List<Int>): List<VocabularyWordEntity> =
        if (ids.isEmpty()) emptyList() else dao.getWordsByIds(ids)

    suspend fun getRandomWords(limit: Int): List<VocabularyWordEntity> = dao.getRandomWords(limit)

    suspend fun getCategoryProgress(category: String): CategoryProgress =
        dao.getCategoryProgress(category, category == "500 Kata Paling Berguna")

    private companion object {
        const val TAG = "VocabularyRepository"
    }
}
