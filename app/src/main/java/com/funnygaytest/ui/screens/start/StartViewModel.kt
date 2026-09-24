package com.funnygaytest.ui.screens.start

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.locale.AppLocaleManager
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.AppLanguage
import com.funnygaytest.utils.enums.EndingType
import com.funnygaytest.utils.helpers.generateNewGameRun
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class StartUiState(
    val isGameStarted: Boolean = false,
    val showDifficulty: Boolean = false,
    val isMuted: Boolean = false,
    val wasPussyModeClicked: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.Default
)

sealed interface StartUiEffect {
    data object NavigateToGame : StartUiEffect
    data object NavigateToLoseResult : StartUiEffect
}

@HiltViewModel
class StartViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager,
    private val firestoreManager: FirestoreManager,
    private val localeManager: AppLocaleManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(
        StartUiState(
            isGameStarted = gameBegun,
            isMuted = isMuted,
            currentLanguage = localeManager.currentLanguage
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<StartUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        if (!gameBegun) {
            health = 100
        }

        viewModelScope.launch {
            firestoreManager.getStats()
                .catch { e ->
                    Timber.e(e, "Ошибка доступа к личному делу исследователя")
                }
                .collect { stats ->
                    val achievements = (stats ?: LabStats()).achievements
                    _uiState.update { state ->
                        state.copy(
                            wasPussyModeClicked = achievements.contains(EndingType.LOSE_PUSSY.id)
                        )
                    }
                }
        }
    }

    fun onResume() {
        _uiState.update {
            it.copy(
                isGameStarted = gameBegun,
                isMuted = isMuted,
                currentLanguage = localeManager.currentLanguage
            )
        }
    }

    fun onStartGameClicked() {
        if (!gameBegun) {
            gameBegun = true
            lastQuestionIndex = 0
            health = 100
            currentQuestionList = generateNewGameRun()
        }
        sendEffect(StartUiEffect.NavigateToGame)
    }

    fun onDifficultyClicked() {
        _uiState.update { it.copy(showDifficulty = true) }
    }

    fun onDifficultyDismissed() {
        _uiState.update { it.copy(showDifficulty = false) }
    }

    fun onEasyDifficultySelected() {
        gameBegun = true
        lastQuestionIndex = 0
        health = 0
        sendEffect(StartUiEffect.NavigateToLoseResult)
    }

    fun onHardDifficultySelected() {
        onDifficultyDismissed()
    }

    fun onLanguageSelected(language: AppLanguage) {
        _uiState.update { it.copy(currentLanguage = language) }
        localeManager.setLanguage(language)
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

    private fun sendEffect(effect: StartUiEffect) {
        viewModelScope.launch { _uiEffect.send(effect) }
    }

}
