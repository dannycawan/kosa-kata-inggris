package com.kosakata.inggris.data.seed

import android.content.Context
import androidx.room.withTransaction
import com.kosakata.inggris.data.local.AppDatabase
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import org.json.JSONArray

object VocabularySeeder {
    suspend fun seedIfNeeded(context: Context, database: AppDatabase) {
        if (database.vocabularyDao().countWords() > 0) return

        val json = context.assets.open("vocabulary_words.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val words = buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val categoriesArray = obj.getJSONArray("categories")
                val categories = buildList {
                    for (j in 0 until categoriesArray.length()) add(categoriesArray.getString(j))
                }.joinToString("|")
                add(
                    VocabularyWordEntity(
                        id = obj.getInt("id"),
                        word = obj.getString("word"),
                        partOfSpeechRaw = obj.optString("partOfSpeechRaw", ""),
                        partOfSpeech = obj.getString("partOfSpeech"),
                        level = obj.getString("level"),
                        meaningId = obj.getString("meaningId"),
                        exampleEn = obj.getString("exampleEn"),
                        exampleId = obj.getString("exampleId"),
                        categories = categories,
                        isCore500 = obj.getBoolean("isCore500")
                    )
                )
            }
        }

        require(words.size == 3000) {
            "vocabulary_words.json harus berisi 3000 entri, ditemukan ${words.size}"
        }

        database.withTransaction {
            words.chunked(500).forEach { database.vocabularyDao().insertAll(it) }
        }
    }
}
