package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Text("Menyiapkan kata...")
        }
        return
    }

    if (words.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tidak ada kata baru", style = MaterialTheme.typography.headlineSmall)
            Text("Semua kata pada kategori ini sudah dipelajari atau belum ada review jatuh tempo.")
        }
        return
    }

    val word = words[index]
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Belajar ${index + 1}/${words.size}", fontWeight = FontWeight.Bold)
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            word.word,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("${word.partOfSpeech} | ${word.level}")
                    }
                    IconButton(
                        onClick = {
                            ttsManager.playWord(
                                word.word,
                                accent = accent,
                                slow = speed == "Slow"
                            )
                        }
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Putar kata")
                    }
                }
                Text("Arti", fontWeight = FontWeight.Bold)
                Text(word.meaningId, style = MaterialTheme.typography.titleLarge)
                Text("Contoh", fontWeight = FontWeight.Bold)
                Text(word.exampleEn)
                Text(word.exampleId, color = MaterialTheme.colorScheme.secondary)
                Row {
                    IconButton(
                        onClick = {
                            ttsManager.playExample(
                                word.exampleEn,
                                accent = accent,
                                slow = speed == "Slow"
                            )
                        }
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = "Putar contoh")
                    }
                    IconButton(onClick = {
                        scope.launch { repository.toggleBookmark(word.id) }
                    }) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Simpan")
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, false)
                        if (index < words.lastIndex) {
                            index++
                        } else {
                            repository.saveSession(
                                category = category,
                                totalWords = words.size,
                                startedAt = startedAt
                            )
                            onOpenQuiz()
                        }
                    }
                }
            ) {
                Text("Masih Belajar")
            }
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, true)
                        if (index < words.lastIndex) {
                            index++
                        } else {
                            repository.saveSession(
                                category = category,
                                totalWords = words.size,
                                startedAt = startedAt
                            )
                            onOpenQuiz()
                        }
                    }
                }
            ) {
                Text("Sudah Tahu")
            }
        }
    }
}
