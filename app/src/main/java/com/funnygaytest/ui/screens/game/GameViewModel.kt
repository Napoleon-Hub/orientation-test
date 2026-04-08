package com.funnygaytest.ui.screens.game

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.models.Answer
import com.funnygaytest.models.Question
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.utils.helpers.generateNewGameRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
}

@HiltViewModel
class GameViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(
        GameUiState(
            currentQuestion = currentQuestionList[lastQuestionIndex],
            questionNumber = lastQuestionIndex + 1,
            totalQuestions = currentQuestionList.size,
            isFinish = lastQuestionIndex == currentQuestionList.lastIndex,
            isMuted = isMuted,
            currentHp = health
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<GameUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    fun onAnswerSelected(answer: Answer) {
        _uiState.update { it.copy(selectedAnswer = answer) }
    }

    fun onNextClicked() {
        val currentState = _uiState.value
        val answer = currentState.selectedAnswer

        if (answer != null && answer.answerResId != 0) {
            if (isConnected) {

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

}