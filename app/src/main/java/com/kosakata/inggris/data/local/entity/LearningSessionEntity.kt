package com.kosakata.inggris.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_sessions")
data class LearningSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startedAt: Long,
    val completedAt: Long? = null,
    val category: String,
    val totalWords: Int,
    val correctAnswers: Int = 0,
    val wrongAnswers: Int = 0
)
