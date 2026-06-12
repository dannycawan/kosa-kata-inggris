/*
 * Tujuan: Menampung hasil agregasi jumlah dan progres kata per kategori.
 * Caller: VocabularyDao dan VocabRepository untuk CategoryScreen.
 * Dependensi: Hasil query agregasi Room.
 * Main Functions: CategoryProgress.
 * Side Effects: Tidak ada.
 */
package com.kosakata.inggris.data.local.model

data class CategoryProgress(
    val totalWords: Int,
    val completedWords: Int
)
