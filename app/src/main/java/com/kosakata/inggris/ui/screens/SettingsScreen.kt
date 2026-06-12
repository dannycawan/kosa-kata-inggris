/*
 * Tujuan: Mengelola pengaturan belajar, audio, review, dan data lokal.
 * Caller: VocabNavigation route settings.
 * Dependensi: VocabRepository, UserPreferences, NumericInputDialog.
 * Main Functions: SettingsScreen.
 * Side Effects: Menulis DataStore dan dapat menghapus progress Room/DataStore lokal.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
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
import com.kosakata.inggris.ui.components.NumericInputDialog
import com.kosakata.inggris.ui.components.ScreenTitle
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: VocabRepository) {
    val scope = rememberCoroutineScope()
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val category by repository.preferences.selectedCategory.collectAsState(initial = "500 Kata Paling Berguna")
    val accent by repository.preferences.audioAccent.collectAsState(initial = "US")
    val speed by repository.preferences.audioSpeed.collectAsState(initial = "Normal")
    val repeat by repository.preferences.repeatCount.collectAsState(initial = 3)
    val delay by repository.preferences.listeningDelay.collectAsState(initial = 2)
    var showResetDialog by remember { mutableStateOf(false) }
    var customTarget by remember { mutableStateOf(false) }
    var customRepeat by remember { mutableStateOf(false) }
    var customDelay by remember { mutableStateOf(false) }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Pengaturan", "Atur pengalaman belajar sesuai ritme harianmu.") }
        item {
            SettingsSection("Learning") {
                ChoiceChips(
                    title = "Daily target",
                    values = listOf(5, 10, 20, 30, 50, 100),
                    selected = target,
                    suffix = " kata",
                    onCustom = { customTarget = true }
                ) { scope.launch { repository.preferences.setDailyTarget(it) } }
                Text("Category preference: $category", fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(vocabCategories) { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { scope.launch { repository.preferences.setCategory(item) } },
                            label = { Text(item) }
                        )
                    }
                }
            }
        }
        item {
            SettingsSection("Audio") {
                Text("Accent", fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("US", "UK")) { value ->
                        FilterChip(
                            selected = accent == value,
                            onClick = { scope.launch { repository.preferences.setAudioAccent(value) } },
                            label = { Text(value) }
                        )
                    }
                }
                Text("Speed", fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Slow", "Normal", "Fast")) { value ->
                        FilterChip(
                            selected = speed == value,
                            onClick = { scope.launch { repository.preferences.setAudioSpeed(value) } },
                            label = { Text(value) }
                        )
                    }
                }
                ChoiceChips(
                    title = "Repeat count",
                    values = listOf(1, 3, 5, 10),
                    selected = repeat,
                    suffix = "x",
                    onCustom = { customRepeat = true }
                ) { scope.launch { repository.preferences.setRepeatCount(it) } }
                ChoiceChips(
                    title = "Listening delay",
                    values = listOf(0, 1, 2, 3, 5, 10),
                    selected = delay,
                    suffix = " sec",
                    onCustom = { customDelay = true }
                ) { scope.launch { repository.preferences.setListeningDelay(it) } }
            }
        }
        item {
            SettingsSection("Review") {
                Text("Review memakai jadwal lokal 1, 3, 7, dan 14 hari sampai kata dikuasai.")
                Text("Jawaban salah mengembalikan kata ke antrean besok.")
            }
        }
        item {
            SettingsSection("Data") {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showResetDialog = true }
                ) { Text("Reset Progress Lokal") }
            }
        }
    }

    if (customTarget) {
        NumericInputDialog("Custom daily target", target, 1..500, "kata", { customTarget = false }) {
            customTarget = false
            scope.launch { repository.preferences.setDailyTarget(it) }
        }
    }
    if (customRepeat) {
        NumericInputDialog("Custom repeat", repeat, 1..100, "kali", { customRepeat = false }) {
            customRepeat = false
            scope.launch { repository.preferences.setRepeatCount(it) }
        }
    }
    if (customDelay) {
        NumericInputDialog("Custom delay", delay, 0..60, "detik", { customDelay = false }) {
            customDelay = false
            scope.launch { repository.preferences.setListeningDelay(it) }
        }
    }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset progress?") },
            text = { Text("Progress belajar, bookmark, sesi, streak, dan statistik audio akan dihapus dari perangkat.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        scope.launch { repository.resetProgress() }
                    }
                ) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Batal") } }
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun ChoiceChips(
    title: String,
    values: List<Int>,
    selected: Int,
    suffix: String,
    onCustom: () -> Unit,
    onSelected: (Int) -> Unit
) {
    Text("$title: $selected$suffix", fontWeight = FontWeight.Bold)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(values) { value ->
            FilterChip(
                selected = selected == value,
                onClick = { onSelected(value) },
                label = { Text("$value$suffix") }
            )
        }
        item {
            FilterChip(
                selected = selected !in values,
                onClick = onCustom,
                label = { Text("Custom") }
            )
        }
    }
}
