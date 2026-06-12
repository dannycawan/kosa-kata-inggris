package com.kosakata.inggris.domain.model

enum class QuizType { GUESS_MEANING, GUESS_WORD }

data class QuizQuestion(
    val wordId: Int,
    val questionText: String,
    val correctAnswer: String,
    val options: List<String>,
    val type: QuizType
)
