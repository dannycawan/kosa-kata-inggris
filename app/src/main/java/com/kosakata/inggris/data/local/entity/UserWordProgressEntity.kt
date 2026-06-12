package com.kosakata.inggris.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_word_progress",
    indices = [
        Index(value = ["status"]),
        Index(value = ["nextReviewAt"]),
        Index(value = ["isBookmarked"])
    ]
)
data class UserWordProgressEntity(
    @PrimaryKey val wordId: Int,
    val status: String = "NEW",
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long? = null,
    val isBookmarked: Boolean = false
)
