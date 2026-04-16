package com.funnygaytest.ui.screens.start

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.EndingType
import com.funnygaytest.utils.helpers.generateNewGameRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class StartUiState(
    val isGameStarted: Boolean = false,
    val showDifficulty: Boolean = false,
    val isMuted: Boolean = false,
    val wasPussyModeClicked: Boolean = false
)

sealed class StartUiEffect {
    object NavigateToGame : StartUiEffect()
}

@HiltViewModel
class StartViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager,
    private val firestoreManager: FirestoreManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(StartUiState(isMuted = isMuted))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<StartUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    init {
        _uiState.update {
            it.copy(isGameStarted = gameBegun)
        }

        if (!gameBegun) {
            health = 100
        }

        viewModelScope.launch {
            firestoreManager.getStats()
                .catch { e ->
                    Timber.e(e, "Ошибка доступа к личному делу исследователя")
                }
                .collect { stats ->
                    val currentStats = stats ?: LabStats()
                    val achievements = currentStats.achievements

                    _uiState.update { state ->
                        state.copy(
                            wasPussyModeClicked = achievements.contains(EndingType.LOSE_PUSSY.id)
                        )
                    }
                }
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