package com.kosakata.inggris.domain.review

import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.domain.model.QuizQuestion
import com.kosakata.inggris.domain.model.QuizType

object QuizGenerator {
    fun generateMixed(
        words: List<VocabularyWordEntity>,
        distractors: List<VocabularyWordEntity>
    ): List<QuizQuestion> {
        return words.flatMapIndexed { index, word ->
            val type = if (index % 2 == 0) QuizType.GUESS_MEANING else QuizType.GUESS_WORD
            listOf(generateOne(word, distractors, type))
        }
    }

    fun generateOne(
        word: VocabularyWordEntity,
        distractors: List<VocabularyWordEntity>,
        type: QuizType
    ): QuizQuestion {
        return when (type) {
            QuizType.GUESS_WORD -> {
                val wrong = distractors
                    .filter { it.id != word.id && it.word.isNotBlank() }
                    .map { it.word }
                    .distinct()
                    .shuffled()
                    .take(3)
                QuizQuestion(
                    wordId = word.id,
                    questionText = "${word.meaningId} dalam bahasa Inggris apa?",
                    correctAnswer = word.word,
                    options = (wrong + word.word).distinct().shuffled(),
                    type = type
                )
            }
            else -> {
                val wrong = distractors
                    .filter { it.id != word.id && it.meaningId.isNotBlank() }
                    .map { it.meaningId }
                    .distinct()
                    .shuffled()
                    .take(3)
                QuizQuestion(
                    wordId = word.id,
                    questionText = "${word.word} artinya apa?",
                    correctAnswer = word.meaningId,
                    options = (wrong + word.meaningId).distinct().shuffled(),
                    type = QuizType.GUESS_MEANING
                )
            }
        }
    }
}
