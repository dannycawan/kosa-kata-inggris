/*
 * Tujuan: Menyajikan flashcard bilingual Version 2 dan merekam hasil sesi belajar.
 * Caller: VocabNavigation route learn.
 * Dependensi: VocabRepository, TtsManager, VocabularyWordEntity.
 * Main Functions: LearningSessionScreen.
 * Side Effects: Membaca Room/DataStore, memutar TTS, menulis progress/bookmark/session.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.launch

@Composable
fun LearningSessionScreen(
    repository: VocabRepository,
    ttsManager: TtsManager,
    onWordsLoaded: (List<VocabularyWordEntity>) -> Unit,
    onOpenQuiz: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val category by repository.preferences.selectedCategory.collectAsState(
        initial = "500 Kata Paling Berguna"
    )
    val accent by repository.preferences.audioAccent.collectAsState(initial = "US")
    val speed by repository.preferences.audioSpeed.collectAsState(initial = "Normal")
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(true) }
    val startedAt = remember { System.currentTimeMillis() }

    LaunchedEffect(target, category) {
        words = repository.getTodayWords(target, category)
        onWordsLoaded(words)
        loading = false
    }

    if (loading) {
        Column(Modifier.fillMaxSize().padding(20.dp)) { Text("Menyiapkan sesi belajar...") }
        return
    }
    if (words.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            EmptyState(
                title = "Sesi hari ini selesai",
                message = "Tidak ada kata baru atau review jatuh tempo pada kategori ini."
            )
        }
        return
    }

    val word = words[index]
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Belajar ${index + 1}/${words.size}", fontWeight = FontWeight.Bold)
            AssistChip(onClick = {}, label = { Text("$category") })
        }
        LinearProgressIndicator(
            progress = { (index + 1f) / words.size },
            modifier = Modifier.fillMaxWidth()
        )
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    word.word,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AssistChip(onClick = {}, label = { Text(word.partOfSpeech) })
                    AssistChip(onClick = {}, label = { Text(word.level) })
                }
                IconButton(onClick = { scope.launch { repository.toggleBookmark(word.id) } }) {
                    Icon(Icons.Default.Bookmark, contentDescription = "Simpan kata")
                }
            }
        }
        AudioContentCard("Kata Inggris", word.word) {
            ttsManager.playWord(word.word, accent, speed)
        }
        AudioContentCard("Arti Indonesia", word.meaningId) {
            ttsManager.playMeaning(word.meaningId, speed)
        }
        AudioContentCard("Contoh Bahasa Inggris", word.exampleEn) {
            ttsManager.playExample(word.exampleEn, accent, speed)
        }
        AudioContentCard("Terjemahan Indonesia", word.exampleId) {
            ttsManager.playTranslation(word.exampleId, speed)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, false)
                        finishOrAdvance(index, words, category, startedAt, repository, onOpenQuiz) {
                            index++
                        }
                    }
                }
            ) { Text("Masih Belajar") }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, true)
                        finishOrAdvance(index, words, category, startedAt, repository, onOpenQuiz) {
                            index++
                        }
                    }
                }
            ) { Text("Sudah Tahu") }
        }
    }
}

@Composable
private fun AudioContentCard(title: String, text: String, onPlay: () -> Unit) {
    var expanded by remember { mutableStateOf(true) }
    Card(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row {
                    IconButton(onClick = onPlay) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Putar $title")
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Tutup" else "Buka"
                    )
                }
            }
            if (expanded) {
                Text(text, style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

private suspend fun finishOrAdvance(
    index: Int,
    words: List<VocabularyWordEntity>,
    category: String,
    startedAt: Long,
    repository: VocabRepository,
    onOpenQuiz: () -> Unit,
    advance: () -> Unit
) {
    if (index < words.lastIndex) {
        advance()
    } else {
        repository.saveSession(category, words.size, startedAt = startedAt)
        onOpenQuiz()
    }
}
