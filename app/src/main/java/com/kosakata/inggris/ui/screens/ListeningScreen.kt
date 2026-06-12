package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import com.kosakata.inggris.audio.TtsManager
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.repository.VocabRepository

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
    val manager = remember { ListeningSessionManager(ttsManager, scope) }
    val playback by manager.state.collectAsState()

    LaunchedEffect(target, category) {
        words = repository.getTodayWords(target, category)
    }
    DisposableEffect(manager) {
        onDispose { manager.release() }
    }

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Dengarkan Kata Hari Ini",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Format: kata, arti, kata ulang, lalu contoh kalimat.")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Total: ${words.size} kata")
                Text(
                    "Posisi: ${(playback.currentIndex + 1).coerceAtMost(words.size)} / ${words.size}"
                )
                Text("Repeat: ${repeat}x | Delay: ${delay}s | Audio: $accent/$speed")
                if (!ttsManager.isReady()) {
                    Text("Jika suara belum keluar, instal data Text-to-Speech bahasa Inggris.")
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                Modifier.weight(1f),
                enabled = words.isNotEmpty(),
                onClick = {
                    if (
                        playback.totalWords > 0 &&
                        !playback.isPlaying &&
                        playback.completedLoops < repeat
                    ) {
                        manager.resume()
                    } else {
                        manager.start(words, repeat, delay, accent, speed == "Slow")
                    }
                }
            ) {
                Text(if (playback.totalWords > 0) "Lanjut" else "Play")
            }
            OutlinedButton(Modifier.weight(1f), onClick = manager::pause) {
                Text("Pause")
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(Modifier.weight(1f), onClick = manager::stop) {
                Text("Stop")
            }
            OutlinedButton(Modifier.weight(1f), onClick = manager::next) {
                Text("Next")
            }
        }
        Text("Autoplay berjalan saat aplikasi terbuka. Background playback belum digunakan.")
    }
}
