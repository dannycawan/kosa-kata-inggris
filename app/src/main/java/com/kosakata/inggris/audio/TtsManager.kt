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

        ready = isLanguageAvailable(Locale.US)
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

    fun playWord(word: String, accent: String = "US", slow: Boolean = false) {
        speak(word, englishLocale(accent), slow)
    }

    fun playExample(sentence: String, accent: String = "US", slow: Boolean = false) {
        speak(sentence, englishLocale(accent), slow)
    }

    fun playMeaning(meaning: String, slow: Boolean = false) {
        speak(meaning, INDONESIAN, slow)
    }

    fun speak(
        text: String,
        locale: Locale = Locale.US,
        slow: Boolean = false
    ) {
        if (!ready || text.isBlank()) {
            onDone?.invoke()
            return
        }

        val selectedLocale = if (isLanguageAvailable(locale)) locale else Locale.US
        tts?.language = selectedLocale
        tts?.setSpeechRate(if (slow) SLOW_RATE else NORMAL_RATE)
        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "tts_${System.nanoTime()}"
        )
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

    private fun isLanguageAvailable(locale: Locale): Boolean {
        val result = tts?.isLanguageAvailable(locale) ?: TextToSpeech.LANG_NOT_SUPPORTED
        return result != TextToSpeech.LANG_MISSING_DATA &&
            result != TextToSpeech.LANG_NOT_SUPPORTED
    }

    private companion object {
        val INDONESIAN = Locale("id", "ID")
        const val NORMAL_RATE = 0.9f
        const val SLOW_RATE = 0.65f
    }
}
