package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.data.repository.VocabRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: VocabRepository) {
    val scope = rememberCoroutineScope()
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val accent by repository.preferences.audioAccent.collectAsState(initial = "US")
    val speed by repository.preferences.audioSpeed.collectAsState(initial = "Normal")
    val repeat by repository.preferences.repeatCount.collectAsState(initial = 3)
    val delay by repository.preferences.listeningDelay.collectAsState(initial = 2)
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "Pengaturan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        SettingChips("Target harian", listOf(5, 10, 20), target, suffix = " kata") {
            scope.launch { repository.preferences.setDailyTarget(it) }
        }
        Text("Aksen audio")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("US", "UK").forEach { value ->
                FilterChip(
                    selected = accent == value,
                    onClick = { scope.launch { repository.preferences.setAudioAccent(value) } },
                    label = { Text(value) }
                )
            }
        }
        Text("Kecepatan audio")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Normal", "Slow").forEach { value ->
                FilterChip(
                    selected = speed == value,
                    onClick = { scope.launch { repository.preferences.setAudioSpeed(value) } },
                    label = { Text(value) }
                )
            }
        }
        SettingChips("Repeat listening", listOf(1, 3, 5), repeat, suffix = "x") {
            scope.launch { repository.preferences.setRepeatCount(it) }
        }
        SettingChips("Delay antar kata", listOf(1, 2, 3), delay, suffix = " detik") {
            scope.launch { repository.preferences.setListeningDelay(it) }
        }
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { showResetDialog = true }
        ) {
            Text("Reset Progress Lokal")
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset progress?") },
            text = { Text("Progress belajar, bookmark, sesi, dan streak akan dihapus dari perangkat.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch { repository.resetProgress() }
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingChips(
    title: String,
    values: List<Int>,
    selected: Int,
    suffix: String,
    onSelected: (Int) -> Unit
) {
    Text(title)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        values.forEach { value ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                label = { Text("$value$suffix") }
            )
        }
    }
}
