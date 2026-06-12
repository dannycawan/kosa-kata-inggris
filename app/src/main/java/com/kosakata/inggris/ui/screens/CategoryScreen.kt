package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.data.repository.VocabRepository
import kotlinx.coroutines.launch

val vocabCategories = listOf(
    "500 Kata Paling Berguna", "A1 Dasar", "A2 Aktif", "B1 Praktis", "B2 Lanjutan",
    "Daily Life", "School", "Work", "Business & Selling", "Digital & Content",
    "Travel", "Health", "Emotion & Communication", "Verbs Only", "Nouns Only", "Adjectives Only"
)

@Composable
fun CategoryScreen(repository: VocabRepository, onStart: () -> Unit) {
    val scope = rememberCoroutineScope()
    val selected by repository.preferences.selectedCategory.collectAsState(initial = "500 Kata Paling Berguna")
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Pilih Kategori", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Kategori aktif: $selected")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            items(vocabCategories) { cat ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(cat, fontWeight = FontWeight.Bold)
                        Button(onClick = { scope.launch { repository.preferences.setCategory(cat); onStart() } }) {
                            Text(if (cat == selected) "Mulai dari sini" else "Pilih & Mulai")
                        }
                    }
                }
            }
        }
    }
}
