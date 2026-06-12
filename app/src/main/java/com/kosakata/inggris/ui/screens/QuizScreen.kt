package com.kosakata.inggris.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
    val startedAt = remember { System.currentTimeMillis() }

    LaunchedEffect(sessionWords, target, category) {
        val sourceWords = sessionWords.ifEmpty { repository.getTodayWords(target, category) }
        questions = QuizGenerator.generateMixed(sourceWords, repository.getRandomWords(100))
    }

    if (questions.isEmpty()) {
        Column(Modifier.fillMaxSize().padding(18.dp)) {
            Text("Menyiapkan quiz...")
        }
        return
    }

    val question = questions[index]
    Column(
        Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Quiz ${index + 1}/${questions.size}", fontWeight = FontWeight.Bold)
        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
            Text(
                question.questionText,
                Modifier.padding(22.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
        question.options.forEach { option ->
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !answering,
                onClick = {
                    answering = true
                    val isCorrect = option == question.correctAnswer
                    val finalScore = score + if (isCorrect) 1 else 0
                    score = finalScore
                    scope.launch {
                        repository.updateQuizProgress(question.wordId, isCorrect)
                        if (index < questions.lastIndex) {
                            index++
                            answering = false
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
            ) {
                Text(option)
            }
        }
    }
}
