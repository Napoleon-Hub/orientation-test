package com.funnygaytest.ui.screens.game

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.R
import com.funnygaytest.data.game.GameSession
import com.funnygaytest.data.game.GameSessionRepository
import com.funnygaytest.model.Answer
import com.funnygaytest.model.Question
import com.funnygaytest.platform.audio.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
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
    val currentHp: Int = GameSession.MAX_HEALTH,
    val maxHp: Int = GameSession.MAX_HEALTH
)

sealed interface GameUiEffect {
    data object NavigateToResultScreen : GameUiEffect
    data class ShowToast(@param:StringRes val messageRes: Int) : GameUiEffect
}

@HiltViewModel
class GameViewModel @Inject constructor(
    musicController: MusicController,
    private val gameSessionRepository: GameSessionRepository
) : ViewModel(), MusicController by musicController {

    private val _uiState = MutableStateFlow<GameUiState?>(null)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<GameUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    private var session = GameSession()
    private var isRunOver = false

    init {
        viewModelScope.launch {
            var loaded = gameSessionRepository.session.first()
            if (!loaded.isValid) {
                Timber.w("Saved run is empty or the question index is invalid, starting a new run")
                _uiEffect.send(GameUiEffect.ShowToast(R.string.error_empty_questions))
                loaded = GameSession.newRun()
                gameSessionRepository.update { loaded }
            }
            session = loaded
            _uiState.value = loaded.toUiState()
        }
    }

    fun onAnswerSelected(answer: Answer) {
        _uiState.update { it?.copy(selectedAnswer = answer) }
    }

    fun onNextClicked() {
        val state = _uiState.value ?: return
        val answer = state.selectedAnswer ?: return
        if (isRunOver || answer.answerResId == 0) return

        val newHp = (state.currentHp + answer.hpChange).coerceIn(0, GameSession.MAX_HEALTH)
        isRunOver = newHp <= 0 || state.isFinish

        session = if (isRunOver) {
            session.copy(health = newHp)
        } else {
            session.copy(health = newHp, questionIndex = session.questionIndex + 1)
        }
        _uiState.value = if (isRunOver) state.copy(currentHp = newHp) else session.toUiState()

        val savedSession = session
        viewModelScope.launch {
            gameSessionRepository.update { savedSession }
            if (isRunOver) _uiEffect.send(GameUiEffect.NavigateToResultScreen)
        }
    }

    private fun GameSession.toUiState() = GameUiState(
        currentQuestion = questions[questionIndex],
        isFinish = questionIndex == questions.lastIndex,
        questionNumber = questionNumber,
        totalQuestions = questions.size,
        currentHp = health
    )

}
