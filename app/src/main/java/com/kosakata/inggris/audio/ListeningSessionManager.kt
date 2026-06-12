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
    val completedLoops: Int = 0
)

class ListeningSessionManager(
    private val ttsManager: TtsManager,
    private val scope: CoroutineScope
) {
    private var words: List<VocabularyWordEntity> = emptyList()
    private var currentIndex = 0
    private var currentStep = 0
    private var completedLoops = 0
    private var repeatCount = 3
    private var delaySeconds = 2
    private var accent = "US"
    private var slow = false
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
        slowMode: Boolean
    ) {
        stop()
        if (items.isEmpty()) return

        words = items
        repeatCount = repeat.coerceIn(1, 5)
        delaySeconds = delayBetweenItems.coerceIn(0, 10)
        accent = audioAccent
        slow = slowMode
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
        delayJob?.cancel()
        ttsManager.stop()
        currentIndex = (currentIndex + 1) % words.size
        currentStep = 0
        if (!playing) playing = true
        updateState()
        playCurrentStep()
    }

    fun release() {
        stop()
        ttsManager.onDone = null
    }

    private fun playCurrentStep() {
        if (!playing || words.isEmpty()) return
        val word = words[currentIndex]
        when (currentStep) {
            0 -> ttsManager.playWord(word.word, accent, slow)
            1 -> ttsManager.playMeaning(word.meaningId, slow)
            2 -> ttsManager.playWord(word.word, accent, slow)
            else -> ttsManager.playExample(word.exampleEn, accent, slow)
        }
    }

    private fun advanceStep() {
        if (!playing || words.isEmpty()) return
        if (currentStep < LAST_STEP) {
            currentStep++
            playCurrentStep()
            return
        }

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
            completedLoops = completedLoops
        )
    }

    private companion object {
        const val LAST_STEP = 3
    }
}
