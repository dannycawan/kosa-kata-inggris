/*
 * Tujuan: Menampilkan library kata dengan pencarian dan filter bookmark/mastered/review.
 * Caller: VocabNavigation route bookmark.
 * Dependensi: VocabRepository, TtsManager, VocabularyWordEntity.
 * Main Functions: BookmarkScreen.
 * Side Effects: Membaca Room, memutar TTS, dan mengubah status bookmark.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.audio.TtsManager
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.ui.components.EmptyState
import com.kosakata.inggris.ui.components.ScreenTitle
import kotlinx.coroutines.launch

private enum class WordFilter(val label: String) {
    ALL("All"),
    BOOKMARKED("Bookmarked"),
    MASTERED("Mastered"),
    NEED_REVIEW("Need Review")
}

@Composable
fun BookmarkScreen(repository: VocabRepository, ttsManager: TtsManager) {
    val scope = rememberCoroutineScope()
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    var filter by remember { mutableStateOf(WordFilter.BOOKMARKED) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(filter) {
        words = when (filter) {
            WordFilter.ALL -> (
                repository.getBookmarkedWords(200) +
                    repository.getMasteredWords(200) +
                    repository.getReviewWords(200)
                ).distinctBy { it.id }
            WordFilter.BOOKMARKED -> repository.getBookmarkedWords(200)
            WordFilter.MASTERED -> repository.getMasteredWords(200)
            WordFilter.NEED_REVIEW -> repository.getReviewWords(200)
        }
    }

    val visible = words.filter {
        query.isBlank() ||
            it.word.contains(query, ignoreCase = true) ||
            it.meaningId.contains(query, ignoreCase = true)
    }

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ScreenTitle("Kata tersimpan", "Cari dan filter daftar kata penting.")
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Cari kata atau arti") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WordFilter.entries.forEach { item ->
                FilterChip(
                    selected = filter == item,
                    onClick = { filter = item },
                    label = { Text(item.label) }
                )
            }
        }
        if (visible.isEmpty()) {
            EmptyState(
                title = "Tidak ada kata",
                message = "Coba filter lain atau simpan kata dari flashcard."
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(visible, key = { it.id }) { word ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(word.word, fontWeight = FontWeight.Bold)
                                Text(word.meaningId, style = MaterialTheme.typography.titleMedium)
                                Text(word.exampleEn, style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { ttsManager.playWord(word.word) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Putar kata")
                            }
                            if (filter == WordFilter.BOOKMARKED || filter == WordFilter.ALL) {
                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            repository.toggleBookmark(word.id)
                                            words = words.filterNot { it.id == word.id }
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.BookmarkRemove, contentDescription = "Hapus bookmark")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
