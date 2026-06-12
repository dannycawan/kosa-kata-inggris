package com.kosakata.inggris.domain.review

import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.domain.model.QuizType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizGeneratorTest {
    private val words = (1..5).map { id ->
        VocabularyWordEntity(
            id = id,
            word = "word$id",
            partOfSpeechRaw = "noun",
            partOfSpeech = "noun",
            level = "A1",
            meaningId = "arti$id",
            exampleEn = "Example $id",
            exampleId = "Contoh $id",
            categories = "A1 Dasar",
            isCore500 = true
        )
    }

    @Test
    fun meaningQuestionHasFourUniqueOptions() {
        val question = QuizGenerator.generateOne(words.first(), words, QuizType.GUESS_MEANING)
        assertEquals(4, question.options.size)
        assertEquals(4, question.options.distinct().size)
        assertTrue(question.correctAnswer in question.options)
    }

    @Test
    fun wordQuestionHasFourUniqueOptions() {
        val question = QuizGenerator.generateOne(words.first(), words, QuizType.GUESS_WORD)
        assertEquals(4, question.options.size)
        assertEquals(4, question.options.distinct().size)
        assertTrue(question.correctAnswer in question.options)
    }
}
