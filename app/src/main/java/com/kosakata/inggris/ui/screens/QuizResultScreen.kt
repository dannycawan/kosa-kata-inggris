/*
 * Tujuan: Menampilkan hasil quiz, akurasi, kata untuk review, dan CTA setelah quiz.
 * Caller: VocabNavigation route quiz-result.
 * Dependensi: Material 3.
 * Main Functions: QuizResultScreen.
 * Side Effects: Memicu callback navigasi; interstitial ditangani oleh VocabNavigation.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Target
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.ui.components.StatCard

@Composable
fun QuizResultScreen(
    correct: Int,
    wrong: Int,
    onContinue: () -> Unit,
    onReview: () -> Unit,
    onHome: () -> Unit
) {
    val total = (correct + wrong).coerceAtLeast(1)
    val accuracy = (correct * 100) / total
    val message = when {
        accuracy >= 90 -> "Hebat! Anda menguasai $correct dari $total kata hari ini."
        accuracy >= 70 -> "Bagus. Sedikit review lagi akan membuatnya makin kuat."
        else -> "Tidak apa-apa. Ulangi kata sulit dan coba lagi setelah review."
    }

    Column(
        Modifier.fillMaxSize().padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Quiz selesai", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Default.Target, contentDescription = null)
                    Text("Skor $correct/$total", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
                Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Icons.Default.CheckCircle, "Benar", correct.toString(), Modifier.weight(1f))
            StatCard(Icons.Default.Refresh, "Salah", wrong.toString(), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Icons.Default.Target, "Akurasi", "$accuracy%", Modifier.weight(1f))
            StatCard(Icons.Default.Refresh, "Perlu review", wrong.toString(), Modifier.weight(1f))
        }
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Lanjut Belajar")
        }
        OutlinedButton(onClick = onReview, modifier = Modifier.fillMaxWidth()) {
            Text("Review Kata Sulit")
        }
        OutlinedButton(onClick = onHome, modifier = Modifier.fillMaxWidth()) {
            Text("Kembali ke Home")
        }
    }
}
