package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
fun HomeScreen(
    repository: VocabRepository,
    onStartLearn: () -> Unit,
    onListen: () -> Unit,
    onReview: () -> Unit
) {
    val total by repository.observeTotalWords().collectAsState(initial = 0)
    val learned by repository.observeLearnedCount().collectAsState(initial = 0)
    val mastered by repository.observeMasteredCount().collectAsState(initial = 0)
    val learning by repository.observeLearningCount().collectAsState(initial = 0)
    val dueReview by repository.observeDueReviewCount().collectAsState(initial = 0)
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val streak by repository.preferences.streakCount.collectAsState(initial = 0)

    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            "3000 Kosakata Inggris",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text("Belajar sedikit setiap hari dan ulangi kata yang masih sulit.")

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Progress", fontWeight = FontWeight.Bold)
                Text("Dipelajari: $learned / $total")
                Text("Mastered: $mastered | Learning: $learning")
                Text("Review hari ini: $dueReview kata")
                Text("Streak: $streak hari")
            }
        }

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Target hari ini", fontWeight = FontWeight.Bold)
                Text("$target kata baru atau review")
                Button(Modifier.fillMaxWidth(), onClick = onStartLearn) {
                    Text("Mulai Belajar")
                }
                OutlinedButton(Modifier.fillMaxWidth(), onClick = onListen) {
                    Text("Dengarkan $target Kata Hari Ini")
                }
                OutlinedButton(Modifier.fillMaxWidth(), onClick = onReview) {
                    Text("Review Kata Sulit")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        BannerAdView(Modifier.fillMaxWidth().height(54.dp))
    }
}
