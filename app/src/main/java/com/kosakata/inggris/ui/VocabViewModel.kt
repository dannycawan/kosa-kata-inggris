package com.kosakata.inggris.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kosakata.inggris.data.local.entity.VocabularyWordEntity
import com.kosakata.inggris.data.repository.VocabRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizResultState(
    val correct: Int = 0,
    val wrong: Int = 0
)

class VocabViewModel(val repository: VocabRepository) : ViewModel() {
    private val _currentSessionWords = MutableStateFlow<List<VocabularyWordEntity>>(emptyList())
    val currentSessionWords: StateFlow<List<VocabularyWordEntity>> = _currentSessionWords.asStateFlow()

    private val _quizResult = MutableStateFlow(QuizResultState())
    val quizResult: StateFlow<QuizResultState> = _quizResult.asStateFlow()

    init {
        viewModelScope.launch { repository.seedIfNeeded() }
    }

    fun setCurrentSession(words: List<VocabularyWordEntity>) {
        _currentSessionWords.value = words
    }

    fun setQuizResult(correct: Int, wrong: Int) {
        _quizResult.value = QuizResultState(correct, wrong)
    }
}

class VocabViewModelFactory(
    private val repository: VocabRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(VocabViewModel::class.java))
        return VocabViewModel(repository) as T
    }
}
