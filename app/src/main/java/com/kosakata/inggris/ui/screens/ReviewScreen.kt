/*
 * Tujuan: Menampilkan antrean review, statistik review, dan kartu kata sulit.
 * Caller: VocabNavigation route review.
 * Dependensi: VocabRepository, TtsManager, EmptyState.
 * Main Functions: ReviewScreen.
 * Side Effects: Membaca/menulis progress Room dan memutar TTS.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Upcoming
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
import com.kosakata.inggris.ui.components.StatCard
import kotlinx.coroutines.launch

@Composable
fun ReviewScreen(repository: VocabRepository, ttsManager: TtsManager) {
    val scope = rememberCoroutineScope()
    val dueToday by repository.observeDueReviewCount().collectAsState(initial = 0)
    val dueTomorrow by repository.observeTomorrowReviewCount().collectAsState(initial = 0)
    val mastered by repository.observeMasteredCount().collectAsState(initial = 0)
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    var sourceLabel by remember { mutableStateOf("jatuh tempo") }

    LaunchedEffect(Unit) {
        val due = repository.getReviewWords()
        words = due.ifEmpty {
            sourceLabel = "sering salah"
            repository.getDifficultWords()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenTitle(
                title = "Review kata",
                subtitle = "Perkuat kata yang jatuh tempo dan kata yang sering salah."
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Schedule, "Review hari ini", dueToday.toString(), Modifier.weight(1f))
                StatCard(Icons.Default.Upcoming, "Besok", dueTomorrow.toString(), Modifier.weight(1f))
            }
        }
        item {
            StatCard(Icons.Default.CheckCircle, "Mastered words", mastered.toString(), Modifier.fillMaxWidth())
        }
        if (words.isEmpty()) {
            item {
                EmptyState(
                    title = "Tidak ada review",
                    message = "Antrean review kosong. Kembali lagi setelah sesi belajar berikutnya.",
                    icon = Icons.Default.CheckCircle
                )
            }
        } else {
            item {
                val word = words.first()
                Text("${words.size} kata $sourceLabel tersisa", fontWeight = FontWeight.Bold)
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(word.word, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { ttsManager.playWord(word.word) }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Putar kata")
                            }
                        }
                        Text(word.meaningId, style = MaterialTheme.typography.titleLarge)
                        Text(word.exampleEn)
                        Text(word.exampleId, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                repository.markSeen(words.first().id, false)
                                words = words.drop(1)
                            }
                        }
                    ) { Text("Masih Sulit") }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                repository.markSeen(words.first().id, true)
                                words = words.drop(1)
                            }
                        }
                    ) { Text("Sudah Ingat") }
                }
            }
        }
    }
}
