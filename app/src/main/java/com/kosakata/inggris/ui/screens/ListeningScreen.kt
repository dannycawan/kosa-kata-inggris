/*
 * Tujuan: Menyediakan pengalaman listening bilingual lima tahap dengan kontrol lengkap.
 * Caller: VocabNavigation route listening.
 * Dependensi: VocabRepository, TtsManager, ListeningSessionManager, DataStore preferences.
 * Main Functions: ListeningScreen.
 * Side Effects: Membaca kata, memutar TTS, menyimpan repeat custom dan words heard.
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
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.kosakata.inggris.audio.ListeningSessionManager
import com.kosakata.inggris.audio.ListeningStage
import com.kosakata.inggris.audio.TtsManager
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.ui.components.EmptyState
import com.kosakata.inggris.ui.components.NumericInputDialog
import com.kosakata.inggris.ui.components.ScreenTitle
import kotlinx.coroutines.launch

@Composable
fun ListeningScreen(repository: VocabRepository, ttsManager: TtsManager) {
    val scope = rememberCoroutineScope()
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val category by repository.preferences.selectedCategory.collectAsState(
        initial = "500 Kata Paling Berguna"
    )
    val accent by repository.preferences.audioAccent.collectAsState(initial = "US")
    val speed by repository.preferences.audioSpeed.collectAsState(initial = "Normal")
    val repeat by repository.preferences.repeatCount.collectAsState(initial = 3)
    val delay by repository.preferences.listeningDelay.collectAsState(initial = 2)
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showCustomRepeat by remember { mutableStateOf(false) }
    val manager = remember(ttsManager) {
        ListeningSessionManager(ttsManager, scope) {
            scope.launch { repository.preferences.recordWordsHeard() }
        }
    }
    val playback by manager.state.collectAsState()
    val currentWord = words.getOrNull(playback.currentIndex)

    LaunchedEffect(target, category) {
        words = repository.getTodayWords(target, category)
        loading = false
    }
    DisposableEffect(manager) {
        onDispose { manager.release() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenTitle(
                title = "Dengarkan hari ini",
                subtitle = "Ikuti kata, arti, contoh, dan terjemahan sambil audio berjalan."
            )
        }
        if (loading) {
            item { Text("Menyiapkan audio...") }
        } else if (currentWord == null) {
            item {
                EmptyState(
                    title = "Belum ada kata",
                    message = "Pilih kategori lain atau selesaikan pengaturan target harian.",
                    icon = Icons.Default.Headphones
                )
            }
        } else {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                currentWord.word,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text("${playback.currentIndex + 1}/${words.size}") }
                            )
                        }
                        Text(currentWord.meaningId, style = MaterialTheme.typography.titleLarge)
                        Text(currentWord.exampleEn)
                        Text(
                            currentWord.exampleId,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Tahap playback", fontWeight = FontWeight.Bold)
                        ListeningStage.entries.forEach { stage ->
                            val active = stage == playback.stage
                            val completed = stage.ordinal < playback.stage.ordinal
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(
                                    if (completed) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = when {
                                        active -> MaterialTheme.colorScheme.primary
                                        completed -> MaterialTheme.colorScheme.secondary
                                        else -> MaterialTheme.colorScheme.outline
                                    }
                                )
                                Text(
                                    stage.label,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
            item {
                Text("Repeat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 3, 5, 10).forEach { value ->
                        FilterChip(
                            selected = repeat == value,
                            onClick = { scope.launch { repository.preferences.setRepeatCount(value) } },
                            label = { Text("${value}x") }
                        )
                    }
                    FilterChip(
                        selected = repeat !in listOf(1, 3, 5, 10),
                        onClick = { showCustomRepeat = true },
                        label = { Text("Custom") }
                    )
                }
                Text(
                    "Aktif: ${repeat}x, jeda ${delay} detik, $accent/$speed",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    IconButton(onClick = manager::previous) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Sebelumnya")
                    }
                    IconButton(
                        onClick = {
                            manager.start(words, repeat, delay, accent, speed)
                        }
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                    }
                    IconButton(onClick = manager::pause, enabled = playback.isPlaying) {
                        Icon(Icons.Default.Pause, contentDescription = "Pause")
                    }
                    IconButton(onClick = manager::resume, enabled = !playback.isPlaying) {
                        Icon(Icons.Default.Replay, contentDescription = "Resume")
                    }
                    IconButton(onClick = manager::stop) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    }
                    IconButton(onClick = manager::next) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Berikutnya")
                    }
                }
            }
            item {
                if (!ttsManager.isReady()) {
                    Text(
                        "Engine suara belum siap. Pastikan data TTS Inggris dan Indonesia tersedia.",
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    OutlinedButton(
                        onClick = { manager.start(words, repeat, delay, accent, speed) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Mulai dari awal")
                    }
                }
            }
        }
    }

    if (showCustomRepeat) {
        NumericInputDialog(
            title = "Repeat custom",
            initialValue = repeat,
            range = 1..100,
            suffix = "kali",
            onDismiss = { showCustomRepeat = false },
            onConfirm = {
                showCustomRepeat = false
                scope.launch { repository.preferences.setRepeatCount(it) }
            }
        )
    }
}
