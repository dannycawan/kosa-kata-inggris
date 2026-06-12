package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
fun ReviewScreen(repository: VocabRepository, ttsManager: TtsManager) {
    val scope = rememberCoroutineScope()
    var words by remember { mutableStateOf<List<VocabularyWordEntity>>(emptyList()) }
    var sourceLabel by remember { mutableStateOf("jatuh tempo") }

    LaunchedEffect(Unit) {
        val due = repository.getReviewWords()
        words = due.ifEmpty {
            sourceLabel = "sering salah"
            repository.getDifficultWords()
        }
    }

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Review Kata Sulit",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text("Menampilkan kata $sourceLabel.")
        if (words.isEmpty()) {
            Text("Belum ada kata yang perlu direview.")
            return@Column
        }

        val word = words.first()
        Text("${words.size} kata tersisa")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(word.word, style = MaterialTheme.typography.headlineMedium)
                    IconButton(onClick = { ttsManager.playWord(word.word) }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Putar kata")
                    }
                }
                Text(word.meaningId, fontWeight = FontWeight.Bold)
                Text(word.exampleEn)
                Text(word.exampleId, color = MaterialTheme.colorScheme.secondary)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, false)
                        words = words.drop(1)
                    }
                }
            ) {
                Text("Masih Sulit")
            }
            Button(
                Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        repository.markSeen(word.id, true)
                        words = words.drop(1)
                    }
                }
            ) {
                Text("Sudah Ingat")
            }
        }
    }
}
