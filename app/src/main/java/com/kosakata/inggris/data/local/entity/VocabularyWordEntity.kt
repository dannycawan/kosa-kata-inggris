package com.kosakata.inggris.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary_words",
    indices = [
        Index(value = ["word"]),
        Index(value = ["level"]),
        Index(value = ["isCore500"])
    ]
)
data class VocabularyWordEntity(
    @PrimaryKey val id: Int,
    val word: String,
    val partOfSpeechRaw: String,
    val partOfSpeech: String,
    val level: String,
    val meaningId: String,
    val exampleEn: String,
    val exampleId: String,
    /** Pipe-separated categories, for example "Daily Life|Verbs Only". */
    val categories: String,
    val isCore500: Boolean
)
