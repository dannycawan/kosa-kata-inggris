/*
 * Tujuan: Menampilkan kategori vocabulary beserta total dan persentase penyelesaian.
 * Caller: VocabNavigation route category.
 * Dependensi: VocabRepository, CategoryProgress, Material icons.
 * Main Functions: CategoryScreen, vocabCategories.
 * Side Effects: Membaca agregasi Room, menyimpan kategori pilihan ke DataStore, lalu navigasi.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.data.local.model.CategoryProgress
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.ui.components.ScreenTitle
import kotlinx.coroutines.launch

val vocabCategories = listOf(
    "500 Kata Paling Berguna", "A1 Dasar", "A2 Aktif", "B1 Praktis", "B2 Lanjutan",
    "Daily Life", "School", "Work", "Business & Selling", "Digital & Content",
    "Travel", "Health", "Emotion & Communication", "Verbs Only", "Nouns Only", "Adjectives Only"
)

@Composable
fun CategoryScreen(repository: VocabRepository, onStart: () -> Unit) {
    val scope = rememberCoroutineScope()
    val selected by repository.preferences.selectedCategory.collectAsState(
        initial = "500 Kata Paling Berguna"
    )
    var progress by remember { mutableStateOf<Map<String, CategoryProgress>>(emptyMap()) }

    LaunchedEffect(Unit) {
        val nextProgress = mutableMapOf<String, CategoryProgress>()
        for (category in vocabCategories) {
            nextProgress[category] = repository.getCategoryProgress(category)
        }
        progress = nextProgress
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            ScreenTitle(
                title = "Pilih kategori",
                subtitle = "Fokuskan sesi pada topik yang paling relevan untukmu."
            )
        }
        items(vocabCategories, key = { it }) { category ->
            val stats = progress[category] ?: CategoryProgress(0, 0)
            val fraction = if (stats.totalWords == 0) 0f
            else stats.completedWords.toFloat() / stats.totalWords
            Card(
                onClick = {
                    scope.launch {
                        repository.preferences.setCategory(category)
                        onStart()
                    }
                },
                colors = CardDefaults.cardColors(
                    containerColor = if (category == selected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    }
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(categoryIcon(category), contentDescription = null)
                        Column(Modifier.weight(1f)) {
                            Text(category, fontWeight = FontWeight.Bold)
                            Text(
                                "${stats.totalWords} kata - ${(fraction * 100).toInt()}% selesai",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (category == selected) {
                            Text("Aktif", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    LinearProgressIndicator(
                        progress = { fraction.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun categoryIcon(category: String): ImageVector = when {
    category.contains("School") || category.contains("Dasar") -> Icons.Default.School
    category.contains("Work") -> Icons.Default.BusinessCenter
    category.contains("Business") -> Icons.Default.ShoppingCart
    category.contains("Digital") -> Icons.Default.PhoneAndroid
    category.contains("Travel") -> Icons.Default.Flight
    category.contains("Health") -> Icons.Default.HealthAndSafety
    category.contains("Emotion") -> Icons.Default.Favorite
    category.contains("500") -> Icons.Default.MenuBook
    else -> Icons.Default.Translate
}
