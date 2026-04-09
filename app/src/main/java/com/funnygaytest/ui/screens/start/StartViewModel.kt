package com.funnygaytest.ui.screens.start

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.helpers.generateNewGameRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StartUiState(
    val isGameStarted: Boolean = false,
    val showDifficulty: Boolean = false,
    val isMuted: Boolean = false,
    val wasPussyModeClicked: Boolean = false,
    val countOfLoses: Int = 0,
    val countOfWins: Int = 0,
)

sealed class StartUiEffect {
    object NavigateToGame : StartUiEffect()
}

@HiltViewModel
class StartViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(StartUiState(isMuted = isMuted))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StartUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        _uiState.update {
            it.copy(
                isGameStarted = gameBegun,
                wasPussyModeClicked = preferences.pussyModeChosen,
                countOfLoses = countOfLoses,
                countOfWins = countOfWins
            )
        }

        if (!gameBegun) {
            health = 100
        }
    }

    fun onNextClicked() {
        if (!gameBegun) {
            gameBegun = true
            lastQuestionIndex = 0
            health = 100
            currentQuestionList = generateNewGameRun()
        }
        viewModelScope.launch {
            _uiEffect.emit(StartUiEffect.NavigateToGame)
            delay(1000)
            _uiState.update { it.copy(isGameStarted = true) }
        }
    }

    fun onDifficultyClicked() {
        _uiState.update { it.copy(showDifficulty = true) }
    }

    fun onDifficultySelected() {
        _uiState.update { it.copy(showDifficulty = false) }
    }

    fun onPermanentLose() {
        preferences.pussyModeChosen = true
        _uiState.update { it.copy(wasPussyModeClicked = true) }

        gameBegun = true
        lastQuestionIndex = 0
        health = 0
    }

    fun updateMutedState() {
        _uiState.update { it.copy(isMuted = isMuted) }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}