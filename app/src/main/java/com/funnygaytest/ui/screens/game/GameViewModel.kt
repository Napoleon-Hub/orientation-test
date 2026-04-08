package com.funnygaytest.ui.screens.game

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.models.Answer
import com.funnygaytest.models.Question
import com.funnygaytest.utils.helpers.QuestionsGenerator
import com.funnygaytest.managers.music.AudioManager
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
            currentQuestion = listOfQuestions[lastQuestionIndex],
            questionNumber = lastQuestionIndex + 1,
            totalQuestions = listOfQuestions.size,
            isFinish = lastQuestionIndex == listOfQuestions.lastIndex,
            isMuted = isMuted
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

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}