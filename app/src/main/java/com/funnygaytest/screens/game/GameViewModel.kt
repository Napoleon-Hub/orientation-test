package com.funnygaytest.screens.game

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.models.Answer
import com.funnygaytest.domain.models.Question
import com.funnygaytest.utils.helpers.QuestionsGenerator
import com.funnygaytest.utils.music.AudioManager
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
    val isMuted: Boolean = false
)

sealed class GameUiEffect {
    object NavigateToResultScreen : GameUiEffect()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val listOfQuestions = QuestionsGenerator().generateQuestions()

    private val _uiState = MutableStateFlow(
        GameUiState(
            currentQuestion = listOfQuestions[preferences.lastQuestionIndex],
            questionNumber = preferences.lastQuestionIndex + 1,
            totalQuestions = listOfQuestions.size,
            isFinish = preferences.lastQuestionIndex == listOfQuestions.lastIndex
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

        if (currentState.selectedAnswer?.answerResId != 0) {
            if (isConnected) {
                points += currentState.selectedAnswer!!.answerPoints

                if (!currentState.isFinish) {
                    changeQuestion()
                } else {
                    viewModelScope.launch { _uiEffect.emit(GameUiEffect.NavigateToResultScreen) }
                }
            }
        }

    }

    private fun changeQuestion() {
        lastQuestionIndex++
        val newIndex = lastQuestionIndex

        _uiState.update {
            it.copy(
                currentQuestion = listOfQuestions[newIndex],
                selectedAnswer = null,
                questionNumber = newIndex + 1,
                isFinish = newIndex == listOfQuestions.lastIndex
            )
        }
    }

    fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
    }

}