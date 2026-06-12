/*
 * Tujuan: Menampilkan dashboard harian, aksi belajar, statistik, dan banner Home.
 * Caller: VocabNavigation route home.
 * Dependensi: VocabRepository, komponen UI bersama, BannerAdView.
 * Main Functions: HomeScreen.
 * Side Effects: Membaca Flow Room/DataStore dan memicu callback navigasi.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.ads.BannerAdView
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.ui.components.DashboardActionCard
import com.kosakata.inggris.ui.components.ScreenTitle
import com.kosakata.inggris.ui.components.StatCard
import java.util.Calendar

@Composable
fun HomeScreen(
    repository: VocabRepository,
    onStartLearn: () -> Unit,
    onListen: () -> Unit,
    onQuiz: () -> Unit,
    onReview: () -> Unit
) {
    val total by repository.observeTotalWords().collectAsState(initial = 0)
    val learned by repository.observeLearnedCount().collectAsState(initial = 0)
    val mastered by repository.observeMasteredCount().collectAsState(initial = 0)
    val dueReview by repository.observeDueReviewCount().collectAsState(initial = 0)
    val bookmarked by repository.observeBookmarkedCount().collectAsState(initial = 0)
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val streak by repository.preferences.streakCount.collectAsState(initial = 0)
    val sessions by repository.observeRecentSessions(100).collectAsState(initial = emptyList())
    val startToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val todayLearning = sessions
        .filter { it.startedAt >= startToday && !it.category.endsWith(" - Quiz") }
        .sumOf { it.totalWords }
        .coerceAtMost(target)
    val todayQuiz = sessions.any { it.startedAt >= startToday && it.category.endsWith(" - Quiz") }
    val dailyProgress = if (target == 0) 0f else todayLearning.toFloat() / target

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ScreenTitle(
                title = "Selamat belajar",
                subtitle = "Bangun kebiasaan kecil yang konsisten setiap hari."
            )
        }
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Streak $streak hari",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        androidx.compose.material3.Icon(
                            Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Text("$todayLearning dari $target kata selesai hari ini")
                    LinearProgressIndicator(
                        progress = { dailyProgress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        item {
            Text("Aktivitas hari ini", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        item {
            DashboardActionCard(
                icon = Icons.Default.MenuBook,
                title = "Mulai Belajar",
                subtitle = "$todayLearning/$target kata target harian",
                progress = dailyProgress,
                onClick = onStartLearn
            )
        }
        item {
            DashboardActionCard(
                icon = Icons.Default.Headphones,
                title = "Dengarkan Hari Ini",
                subtitle = "Latih kata, arti, dan contoh dalam dua bahasa",
                progress = dailyProgress,
                onClick = onListen
            )
        }
        item {
            DashboardActionCard(
                icon = Icons.Default.Quiz,
                title = "Quiz Hari Ini",
                subtitle = if (todayQuiz) "Quiz hari ini sudah selesai" else "Uji pemahaman dari sesi terbaru",
                progress = if (todayQuiz) 1f else 0f,
                onClick = onQuiz
            )
        }
        item {
            DashboardActionCard(
                icon = Icons.Default.Refresh,
                title = "Review Kata Sulit",
                subtitle = "$dueReview kata perlu ditinjau",
                progress = if (dueReview == 0) 1f else 0f,
                onClick = onReview
            )
        }
        item {
            Text("Statistik", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.School, "Kata dipelajari", "$learned/$total", Modifier.weight(1f))
                StatCard(Icons.Default.MilitaryTech, "Kata dikuasai", mastered.toString(), Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(Icons.Default.Bookmark, "Kata tersimpan", bookmarked.toString(), Modifier.weight(1f))
                StatCard(Icons.Default.Refresh, "Perlu review", dueReview.toString(), Modifier.weight(1f))
            }
        }
        item {
            BannerAdView(Modifier.fillMaxWidth().height(54.dp))
        }
    }
}
