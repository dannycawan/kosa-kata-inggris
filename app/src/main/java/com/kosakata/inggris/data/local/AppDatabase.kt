package com.kosakata.inggris.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kosakata.inggris.data.local.dao.LearningSessionDao
import com.kosakata.inggris.data.local.dao.UserProgressDao
import com.kosakata.inggris.data.local.dao.VocabularyDao
import com.kosakata.inggris.data.local.entity.LearningSessionEntity
import com.kosakata.inggris.data.local.entity.UserWordProgressEntity
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity

@Database(
    entities = [VocabularyWordEntity::class, UserWordProgressEntity::class, LearningSessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun learningSessionDao(): LearningSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vocab_inggris.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
