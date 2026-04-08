package com.funnygaytest.ui.screens.start

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.managers.music.AudioManager
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
    val isMuted: Boolean = false
)

sealed class StartUiEffect {
    object NavigateToGame : StartUiEffect()
}

@HiltViewModel
class StartViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(StartUiState(isMuted = isMuted))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StartUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        _uiState.update { it.copy(isGameStarted = gameBegun) }

        if (!gameBegun) {
            points = 0
        }
    }

    fun onNextClicked() {
        if (isConnected) {
            if (!gameBegun) {
                gameBegun = true
                points = 0
            }
            viewModelScope.launch {
                _uiEffect.emit(StartUiEffect.NavigateToGame)
                delay(1000)
                _uiState.update { it.copy(isGameStarted = true) }
            }
        }
    }

    fun onDifficultyClicked() {
        _uiState.update { it.copy(showDifficulty = true) }
    }

    fun onDifficultySelected() {
        _uiState.update { it.copy(showDifficulty = false) }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}