package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.ads.BannerAdView
import com.kosakata.inggris.data.repository.VocabRepository

@Composable
fun ProfileScreen(repository: VocabRepository, onBookmarks: () -> Unit, onSettings: () -> Unit) {
    val total by repository.observeTotalWords().collectAsState(initial = 0)
    val mastered by repository.observeMasteredCount().collectAsState(initial = 0)
    val learned by repository.observeLearnedCount().collectAsState(initial = 0)
    val learning by repository.observeLearningCount().collectAsState(initial = 0)
    val review by repository.observeDueReviewCount().collectAsState(initial = 0)
    val streak by repository.preferences.streakCount.collectAsState(initial = 0)
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val category by repository.preferences.selectedCategory.collectAsState(initial = "500 Kata Paling Berguna")

    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Profil & Progress", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Statistik", fontWeight = FontWeight.Bold)
                Text("Dipelajari: $learned / $total")
                Text("Mastered: $mastered")
                Text("Learning: $learning")
                Text("Review jatuh tempo: $review")
                Text("Streak: $streak hari")
                Text("Target: $target kata/hari")
                Text("Kategori: $category")
            }
        }
        Button(onClick = onBookmarks, modifier = Modifier.fillMaxWidth()) {
            Text("Kata Tersimpan")
        }
        OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Pengaturan")
        }
        BannerAdView(Modifier.fillMaxWidth().height(54.dp))
    }
}
