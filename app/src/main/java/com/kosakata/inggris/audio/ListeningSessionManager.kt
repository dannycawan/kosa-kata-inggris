/*
 * Tujuan: Mengelola autoplay listening lima tahap dengan kontrol navigasi lengkap.
 * Caller: ListeningScreen.
 * Dependensi: TtsManager, VocabularyWordEntity, coroutine scope.
 * Main Functions: start, pause, resume, stop, previous, next, release.
 * Side Effects: Memicu TTS, delay coroutine, dan callback statistik kata terdengar.
 */
package com.kosakata.inggris.audio

import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ListeningState(
    val currentIndex: Int = 0,
    val totalWords: Int = 0,
    val isPlaying: Boolean = false,
    val completedLoops: Int = 0,
    val stage: ListeningStage = ListeningStage.WORD
)

enum class ListeningStage(val label: String) {
    WORD("Kata"),
    MEANING("Arti"),
    WORD_REPEAT("Kata Ulang"),
    EXAMPLE("Contoh"),
    TRANSLATION("Terjemahan")
}

class ListeningSessionManager(
    private val ttsManager: TtsManager,
    private val scope: CoroutineScope,
    private val onWordCompleted: () -> Unit = {}
) {
    private var words: List<VocabularyWordEntity> = emptyList()
    private var currentIndex = 0
    private var currentStep = 0
    private var completedLoops = 0
    private var repeatCount = 3
    private var delaySeconds = 2
    private var accent = "US"
    private var speed = "Normal"
    private var playing = false
    private var delayJob: Job? = null

    private val _state = MutableStateFlow(ListeningState())
    val state: StateFlow<ListeningState> = _state.asStateFlow()

    init {
        ttsManager.onDone = ::advanceStep
    }

    fun start(
        items: List<VocabularyWordEntity>,
        repeat: Int,
        delayBetweenItems: Int,
        audioAccent: String,
        audioSpeed: String
    ) {
        stop()
        if (items.isEmpty()) return

        words = items
        repeatCount = repeat.coerceIn(1, 100)
        delaySeconds = delayBetweenItems.coerceIn(0, 60)
        accent = audioAccent
        speed = audioSpeed
        playing = true
        updateState()
        playCurrentStep()
    }

    fun pause() {
        if (!playing) return
        playing = false
        delayJob?.cancel()
        ttsManager.stop()
        updateState()
    }

    fun resume() {
        if (words.isEmpty() || playing) return
        playing = true
        updateState()
        playCurrentStep()
    }

    fun stop() {
        playing = false
        delayJob?.cancel()
        ttsManager.stop()
        currentIndex = 0
        currentStep = 0
        completedLoops = 0
        updateState()
    }

    fun next() {
        if (words.isEmpty()) return
        moveTo((currentIndex + 1) % words.size)
    }

    fun previous() {
        if (words.isEmpty()) return
        moveTo(if (currentIndex == 0) words.lastIndex else currentIndex - 1)
    }

    fun release() {
        stop()
        ttsManager.onDone = null
    }

    private fun playCurrentStep() {
        if (!playing || words.isEmpty()) return
        val word = words[currentIndex]
        when (ListeningStage.entries[currentStep]) {
            ListeningStage.WORD -> ttsManager.playWord(word.word, accent, speed)
            ListeningStage.MEANING -> ttsManager.playMeaning(word.meaningId, speed)
            ListeningStage.WORD_REPEAT -> ttsManager.playWord(word.word, accent, speed)
            ListeningStage.EXAMPLE -> ttsManager.playExample(word.exampleEn, accent, speed)
            ListeningStage.TRANSLATION -> ttsManager.playTranslation(word.exampleId, speed)
        }
    }

    private fun advanceStep() {
        if (!playing || words.isEmpty()) return
        if (currentStep < ListeningStage.entries.lastIndex) {
            currentStep++
            updateState()
            playCurrentStep()
            return
        }

        onWordCompleted()
        currentStep = 0
        delayJob?.cancel()
        delayJob = scope.launch {
            delay(delaySeconds * 1_000L)
            currentIndex++
            if (currentIndex >= words.size) {
                currentIndex = 0
                completedLoops++
                if (completedLoops >= repeatCount) {
                    playing = false
                    updateState()
                    return@launch
                }
            }
            updateState()
            playCurrentStep()
        }
    }

    private fun updateState() {
        _state.value = ListeningState(
            currentIndex = currentIndex,
            totalWords = words.size,
            isPlaying = playing,
            completedLoops = completedLoops,
            stage = ListeningStage.entries[currentStep]
        )
    }

    private fun moveTo(index: Int) {
        if (words.isEmpty()) return
        delayJob?.cancel()
        ttsManager.stop()
        currentIndex = index
        currentStep = 0
        playing = true
        updateState()
        playCurrentStep()
    }
}
