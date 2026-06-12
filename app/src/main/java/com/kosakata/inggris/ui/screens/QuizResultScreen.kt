package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.ads.BannerAdView

@Composable
fun QuizResultScreen(
    correct: Int,
    wrong: Int,
    onContinue: () -> Unit,
    onReview: () -> Unit,
    onHome: () -> Unit
) {
    val total = correct + wrong
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Quiz selesai",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(10.dp))
        Text("Benar: $correct")
        Text("Salah: $wrong")
        Text("Skor: $correct / $total")
        Spacer(Modifier.height(18.dp))
        BannerAdView(Modifier.fillMaxWidth().height(54.dp))
        Spacer(Modifier.height(18.dp))
        Button(Modifier.fillMaxWidth(), onClick = onContinue) {
            Text("Lanjut Belajar")
        }
        OutlinedButton(Modifier.fillMaxWidth(), onClick = onReview) {
            Text("Review Kata Sulit")
        }
        OutlinedButton(Modifier.fillMaxWidth(), onClick = onHome) {
            Text("Kembali ke Beranda")
        }
    }
}
