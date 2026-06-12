/*
 * Tujuan: Menampilkan dashboard statistik pengguna dan shortcut library/settings.
 * Caller: VocabNavigation route profile.
 * Dependensi: VocabRepository, UserPreferences, BannerAdView, komponen UI bersama.
 * Main Functions: ProfileScreen.
 * Side Effects: Membaca Flow Room/DataStore dan memicu callback navigasi.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Target
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.ads.BannerAdView
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.ui.components.ScreenTitle
import com.kosakata.inggris.ui.components.StatCard

@Composable
fun ProfileScreen(repository: VocabRepository, onBookmarks: () -> Unit, onSettings: () -> Unit) {
    val total by repository.observeTotalWords().collectAsState(initial = 0)
    val mastered by repository.observeMasteredCount().collectAsState(initial = 0)
    val learned by repository.observeLearnedCount().collectAsState(initial = 0)
    val review by repository.observeDueReviewCount().collectAsState(initial = 0)
    val streak by repository.preferences.streakCount.collectAsState(initial = 0)
    val longestStreak by repository.preferences.longestStreak.collectAsState(initial = 0)
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val wordsHeard by repository.preferences.wordsHeard.collectAsState(initial = 0)
    val sessions by repository.observeRecentSessions(100).collectAsState(initial = emptyList())
    val quizSessions = sessions.filter { it.category.endsWith(" - Quiz") }
    val quizCorrect = quizSessions.sumOf { it.correctAnswers }
    val quizTotal = quizSessions.sumOf { it.correctAnswers + it.wrongAnswers }
    val accuracy = if (quizTotal == 0) 0 else (quizCorrect * 100) / quizTotal

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { ScreenTitle("Profil", "Pantau progres dan kebiasaan belajarmu.") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.TrendingUp, "Total learned", "$learned/$total", Modifier.weight(1f))
                StatCard(Icons.Default.CheckCircle, "Total mastered", mastered.toString(), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Whatshot, "Current streak", "$streak hari", Modifier.weight(1f))
                StatCard(Icons.Default.Whatshot, "Longest streak", "$longestStreak hari", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Refresh, "Review queue", review.toString(), Modifier.weight(1f))
                StatCard(Icons.Default.Target, "Daily goal", "$target kata", Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Headphones, "Words heard", wordsHeard.toString(), Modifier.weight(1f))
                StatCard(Icons.Default.CheckCircle, "Quiz accuracy", "$accuracy%", Modifier.weight(1f))
            }
        }
        item {
            Button(onClick = onBookmarks, modifier = Modifier.fillMaxWidth()) {
                Text("Buka Kata Tersimpan")
            }
            OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
                androidx.compose.material3.Icon(Icons.Default.Settings, contentDescription = null)
                Text("Pengaturan")
            }
        }
        item {
            BannerAdView(Modifier.fillMaxWidth().height(54.dp))
        }
    }
}
