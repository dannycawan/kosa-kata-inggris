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
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import kotlinx.coroutines.launch

@Composable
fun BookmarkScreen(repository: VocabRepository, ttsManager: TtsManager) {
    val scope = rememberCoroutineScope()
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    LaunchedEffect(Unit) { words = repository.getBookmarkedWords() }

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Kata Tersimpan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        if (words.isEmpty()) Text("Belum ada kata yang disimpan.")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(words, key = { it.id }) { word ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(14.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(word.word, fontWeight = FontWeight.Bold)
                            Text(word.meaningId)
                            Text(word.exampleEn, style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { ttsManager.playWord(word.word) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Putar kata")
                        }
                        IconButton(
                            onClick = {
                                scope.launch {
                                    repository.toggleBookmark(word.id)
                                    words = words.filterNot { it.id == word.id }
                                }
                            }
                        ) {
                            Icon(Icons.Default.BookmarkRemove, contentDescription = "Hapus simpanan")
                        }
                    }
                }
            }
        }
    }
}
