package com.funnygaytest.ui.screens.game

import androidx.annotation.StringRes
import androidx.lifecycle.viewModelScope
import com.funnygaytest.R
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.models.Answer
import com.funnygaytest.models.Question
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.helpers.generateNewGameRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class GameUiState(
    val currentQuestion: Question,
    val selectedAnswer: Answer? = null,
    val isFinish: Boolean = false,
    val questionNumber: Int = 1,
    val totalQuestions: Int = 1,
    val isMuted: Boolean = false,
    val currentHp: Int = 100,
    val maxHp: Int = 100
)

sealed class GameUiEffect {
    object NavigateToResultScreen : GameUiEffect()
    data class ShowToast(@param:StringRes val messageRes: Int) : GameUiEffect()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(
        GameUiState(
            currentQuestion = currentQuestionList.getOrNull(lastQuestionIndex) ?: getEmptyQuestion(),
            questionNumber = lastQuestionIndex + 1,
            totalQuestions = currentQuestionList.size.coerceAtLeast(1),
            isFinish = currentQuestionList.isNotEmpty() && lastQuestionIndex == currentQuestionList.lastIndex,
            isMuted = isMuted,
            currentHp = health
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<GameUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        if (currentQuestionList.isEmpty() || lastQuestionIndex >= currentQuestionList.size) {
            Timber.w("Process Death detected! List is empty or index is invalid.")
            viewModelScope.launch {
                _uiEffect.emit(GameUiEffect.ShowToast(R.string.error_empty_questions))
            }
            recoverGameState()
        }
    }

    fun onAnswerSelected(answer: Answer) {
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun onNextClicked() {
        val currentState = _uiState.value
        val answer = currentState.selectedAnswer

        if (answer != null && answer.answerResId != 0) {
            val newHp = (currentState.currentHp + answer.hpChange).coerceIn(0, 100)
            health = newHp

            if (newHp <= 0) {
                _uiState.update { it.copy(currentHp = 0) }
                viewModelScope.launch { _uiEffect.emit(GameUiEffect.NavigateToResultScreen) }
            } else if (!currentState.isFinish) {
                changeQuestion(newHp)
            } else {
                _uiState.update { it.copy(currentHp = newHp) }
                viewModelScope.launch { _uiEffect.emit(GameUiEffect.NavigateToResultScreen) }
            }
        }
    }

    private fun changeQuestion(newHp: Int) {
        lastQuestionIndex++
        val newIndex = lastQuestionIndex

        _uiState.update {
            it.copy(
                currentHp = newHp,
                currentQuestion = currentQuestionList[newIndex],
                selectedAnswer = null,
                questionNumber = newIndex + 1,
                isFinish = newIndex == currentQuestionList.lastIndex
            )
        }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

    private fun recoverGameState() {
        currentQuestionList = generateNewGameRun()
        lastQuestionIndex = 0
        health = 100
        _uiState.update {
            it.copy(
                currentQuestion = currentQuestionList[lastQuestionIndex],
                questionNumber = lastQuestionIndex + 1,
                totalQuestions = currentQuestionList.size.coerceAtLeast(1),
                selectedAnswer = null,
                isMuted = isMuted,
                currentHp = health,
            )
        }
    }

    private fun getEmptyQuestion(): Question {
        return Question(id = "error", questionResId = R.string.error, listOfAnswers = emptyList())
    }

}