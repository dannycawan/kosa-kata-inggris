/*
 * Tujuan: Menyediakan playback TTS aman untuk konten Inggris dan Indonesia.
 * Caller: LearningSessionScreen, ListeningSessionManager, ReviewScreen, BookmarkScreen.
 * Dependensi: Android TextToSpeech dan Locale perangkat.
 * Main Functions: playWord, playMeaning, playExample, playTranslation, speak, stop, shutdown.
 * Side Effects: Memutar audio melalui engine TextToSpeech perangkat.
 */
package com.kosakata.inggris.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var ready = false
    var onDone: (() -> Unit)? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return

        ready = true
        tts?.setSpeechRate(NORMAL_RATE)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onError(utteranceId: String?) {
                onDone?.invoke()
            }

            override fun onDone(utteranceId: String?) {
                onDone?.invoke()
            }
        })
    }

    fun isReady(): Boolean = ready

    fun playWord(word: String, accent: String = "US", speed: String = "Normal") {
        speak(word, englishLocale(accent), speed)
    }

    fun playExample(sentence: String, accent: String = "US", speed: String = "Normal") {
        speak(sentence, englishLocale(accent), speed)
    }

    fun playMeaning(meaning: String, speed: String = "Normal") {
        speak(meaning, INDONESIAN, speed)
    }

    fun playTranslation(translation: String, speed: String = "Normal") {
        speak(translation, INDONESIAN, speed)
    }

    fun speak(
        text: String,
        locale: Locale = Locale.US,
        speed: String = "Normal"
    ) {
        if (!ready || text.isBlank()) {
            onDone?.invoke()
            return
        }

        runCatching {
            val selectedLocale = when {
                isLanguageAvailable(locale) -> locale
                isLanguageAvailable(Locale.US) -> Locale.US
                else -> {
                    onDone?.invoke()
                    return
                }
            }
            tts?.language = selectedLocale
            tts?.setSpeechRate(speedRate(speed))
            tts?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                "tts_${System.nanoTime()}"
            )
        }.onFailure {
            onDone?.invoke()
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }

    private fun englishLocale(accent: String): Locale =
        if (accent.equals("UK", ignoreCase = true)) Locale.UK else Locale.US

    private fun speedRate(speed: String): Float = when {
        speed.equals("Slow", ignoreCase = true) -> SLOW_RATE
        speed.equals("Fast", ignoreCase = true) -> FAST_RATE
        else -> NORMAL_RATE
    }

    private fun isLanguageAvailable(locale: Locale): Boolean {
        val result = tts?.isLanguageAvailable(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
        return result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    private companion object {
        val INDONESIAN = Locale("id", "ID")
        const val NORMAL_RATE = 0.95f
        const val SLOW_RATE = 0.7f
        const val FAST_RATE = 1.2f
    }
}
