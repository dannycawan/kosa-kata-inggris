package com.kosakata.inggris.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.TimeZone
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {
    private object Keys {
        val DAILY_TARGET = intPreferencesKey("daily_target")
        val SELECTED_GOAL = stringPreferencesKey("selected_goal")
        val SELECTED_CATEGORY = stringPreferencesKey("selected_category")
        val STREAK_COUNT = intPreferencesKey("streak_count")
        val LAST_STUDY_DATE = longPreferencesKey("last_study_date")
        val AUDIO_ACCENT = stringPreferencesKey("audio_accent")
        val AUDIO_SPEED = stringPreferencesKey("audio_speed")
        val REPEAT_COUNT = intPreferencesKey("repeat_count")
        val LISTENING_DELAY = intPreferencesKey("listening_delay")
        val FIRST_OPEN_DONE = booleanPreferencesKey("first_open_done")
    }

    val dailyTarget: Flow<Int> = context.dataStore.data.map { it[Keys.DAILY_TARGET] ?: 10 }
    val selectedGoal: Flow<String> = context.dataStore.data.map { it[Keys.SELECTED_GOAL] ?: "Pemula" }
    val selectedCategory: Flow<String> = context.dataStore.data.map { it[Keys.SELECTED_CATEGORY] ?: "500 Kata Paling Berguna" }
    val streakCount: Flow<Int> = context.dataStore.data.map { it[Keys.STREAK_COUNT] ?: 0 }
    val lastStudyDate: Flow<Long> = context.dataStore.data.map { it[Keys.LAST_STUDY_DATE] ?: 0L }
    val audioAccent: Flow<String> = context.dataStore.data.map { it[Keys.AUDIO_ACCENT] ?: "US" }
    val audioSpeed: Flow<String> = context.dataStore.data.map { it[Keys.AUDIO_SPEED] ?: "Normal" }
    val repeatCount: Flow<Int> = context.dataStore.data.map { it[Keys.REPEAT_COUNT] ?: 3 }
    val listeningDelay: Flow<Int> = context.dataStore.data.map { it[Keys.LISTENING_DELAY] ?: 2 }
    val firstOpenDone: Flow<Boolean> = context.dataStore.data.map { it[Keys.FIRST_OPEN_DONE] ?: false }

    suspend fun setDailyTarget(value: Int) = context.dataStore.edit { it[Keys.DAILY_TARGET] = value }
    suspend fun setGoal(value: String) = context.dataStore.edit { it[Keys.SELECTED_GOAL] = value }
    suspend fun setCategory(value: String) = context.dataStore.edit { it[Keys.SELECTED_CATEGORY] = value }
    suspend fun setAudioAccent(value: String) = context.dataStore.edit { it[Keys.AUDIO_ACCENT] = value }
    suspend fun setAudioSpeed(value: String) = context.dataStore.edit { it[Keys.AUDIO_SPEED] = value }
    suspend fun setRepeatCount(value: Int) = context.dataStore.edit { it[Keys.REPEAT_COUNT] = value }
    suspend fun setListeningDelay(value: Int) = context.dataStore.edit { it[Keys.LISTENING_DELAY] = value }
    suspend fun setFirstOpenDone(value: Boolean) = context.dataStore.edit { it[Keys.FIRST_OPEN_DONE] = value }
    suspend fun setStreak(count: Int, lastStudyDate: Long) = context.dataStore.edit {
        it[Keys.STREAK_COUNT] = count
        it[Keys.LAST_STUDY_DATE] = lastStudyDate
    }

    suspend fun recordStudyDay(now: Long = System.currentTimeMillis()) {
        val todayEpochDay = localEpochDay(now)
        val previousEpochDay = lastStudyDate.first()
        val currentStreak = streakCount.first()
        val nextStreak = when {
            previousEpochDay == todayEpochDay -> currentStreak
            previousEpochDay == todayEpochDay - 1L -> currentStreak + 1
            else -> 1
        }
        setStreak(nextStreak, todayEpochDay)
    }

    private fun localEpochDay(timestamp: Long): Long {
        val offset = TimeZone.getDefault().getOffset(timestamp)
        return TimeUnit.MILLISECONDS.toDays(timestamp + offset)
    }
}
