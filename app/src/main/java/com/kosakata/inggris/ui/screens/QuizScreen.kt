/*
 * Tujuan: Menampilkan quiz Version 2 dengan progres, skor, dan feedback sebelum lanjut.
 * Caller: VocabNavigation route quiz.
 * Dependensi: VocabRepository, QuizGenerator, VocabularyWordEntity.
 * Main Functions: QuizScreen.
 * Side Effects: Membaca kata acak, menulis progress jawaban, menyimpan sesi quiz.
 */
package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.repository.VocabRepository
import com.kosakata.inggris.domain.model.QuizQuestion
import com.kosakata.inggris.domain.review.QuizGenerator
import com.kosakata.inggris.ui.components.EmptyState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    repository: VocabRepository,
    sessionWords: List<VocabularyWordEntity>,
    onDone: (correct: Int, wrong: Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    val target by repository.preferences.dailyTarget.collectAsState(initial = 10)
    val category by repository.preferences.selectedCategory.collectAsState(
        initial = "500 Kata Paling Berguna"
    )
    var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answering by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val startedAt = remember { System.currentTimeMillis() }

    LaunchedEffect(sessionWords, target, category) {
        val sourceWords = sessionWords.ifEmpty { repository.getTodayWords(target, category) }
        questions = QuizGenerator.generateMixed(sourceWords, repository.getRandomWords(100))
    }

    if (questions.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            EmptyState("Quiz belum siap", "Mulai sesi belajar dulu atau pilih kategori lain.")
        }
        return
    }

    val question = questions[index]
    val progress = (index + 1f) / questions.size
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Quiz Hari Ini", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Soal ${index + 1}/${questions.size}")
            Text("Skor $score | Sisa ${questions.size - index - 1}")
        }
        LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
        Card(shape = RoundedCornerShape(28.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                question.questionText,
                Modifier.padding(24.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        feedback?.let {
            Text(
                it,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (it.startsWith("Benar")) {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
        question.options.forEach { option ->
            val selected = feedback != null && option == question.correctAnswer
            val buttonModifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            if (selected) {
                Button(
                    modifier = buttonModifier,
                    enabled = false,
                    onClick = {}
                ) { Text(option, style = MaterialTheme.typography.titleMedium) }
            } else {
                OutlinedButton(
                    modifier = buttonModifier,
                    enabled = !answering,
                    onClick = {
                        answering = true
                        val isCorrect = option == question.correctAnswer
                        val finalScore = score + if (isCorrect) 1 else 0
                        feedback = if (isCorrect) "Benar" else "Salah - jawaban: ${question.correctAnswer}"
                        scope.launch {
                            repository.updateQuizProgress(question.wordId, isCorrect)
                            delay(900)
                            if (index < questions.lastIndex) {
                                score = finalScore
                                index++
                                answering = false
                                feedback = null
                            } else {
                                val wrong = questions.size - finalScore
                                repository.saveSession(
                                    category = "$category - Quiz",
                                    totalWords = questions.size,
                                    correct = finalScore,
                                    wrong = wrong,
                                    startedAt = startedAt
                                )
                                onDone(finalScore, wrong)
                            }
                        }
                    }
                ) { Text(option, style = MaterialTheme.typography.titleMedium) }
            }
        }
    }
}
