package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
fun OnboardingScreen(repository: VocabRepository, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    val goals = listOf("Pemula", "Sekolah", "Kerja", "Bisnis & Jualan", "Digital & Content", "Travel")
    var selectedGoal by remember { mutableStateOf("Pemula") }
    var target by remember { mutableIntStateOf(10) }

    Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Mulai belajar Inggris", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Pilih tujuan dan target harianmu. Semua progress tersimpan lokal di HP.")
        Text("Tujuan belajar", fontWeight = FontWeight.Bold)
        goals.forEach { goal ->
            FilterChip(selected = selectedGoal == goal, onClick = { selectedGoal = goal }, label = { Text(goal) })
        }
        Text("Target harian", fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 20).forEach { value ->
                FilterChip(selected = target == value, onClick = { target = value }, label = { Text("$value kata") })
            }
        }
        Spacer(Modifier.weight(1f))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    repository.preferences.setGoal(selectedGoal)
                    repository.preferences.setCategory(goalToCategory(selectedGoal))
                    repository.preferences.setDailyTarget(target)
                    repository.preferences.setFirstOpenDone(true)
                    onDone()
                }
            }
        ) { Text("Mulai") }
    }
}

private fun goalToCategory(goal: String): String = when (goal) {
    "Sekolah" -> "School"
    "Kerja" -> "Work"
    "Bisnis & Jualan" -> "Business & Selling"
    "Digital & Content" -> "Digital & Content"
    "Travel" -> "Travel"
    else -> "500 Kata Paling Berguna"
}
