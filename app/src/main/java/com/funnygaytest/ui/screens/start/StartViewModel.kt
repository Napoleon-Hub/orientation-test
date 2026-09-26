package com.funnygaytest.ui.screens.start

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.funnygaytest.data.game.GameSession
import com.funnygaytest.data.game.GameSessionRepository
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.locale.AppLocaleManager
import com.funnygaytest.managers.music.MusicController
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.utils.enums.AppLanguage
import com.funnygaytest.utils.enums.EndingType
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
    val wasPussyModeClicked: Boolean = false,
    val currentLanguage: AppLanguage = AppLanguage.Default
)

sealed interface StartUiEffect {
    data object NavigateToGame : StartUiEffect
    data object NavigateToLoseResult : StartUiEffect
}

@HiltViewModel
class StartViewModel @Inject constructor(
    musicController: MusicController,
    private val gameSessionRepository: GameSessionRepository,
    private val firestoreManager: FirestoreManager,
    private val localeManager: AppLocaleManager
) : ViewModel(), MusicController by musicController {

    private val _uiState = MutableStateFlow(StartUiState(currentLanguage = localeManager.currentLanguage))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<StartUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            gameSessionRepository.session.collect { session ->
                _uiState.update { it.copy(isGameStarted = session.isStarted) }
            }
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
        _uiState.update { it.copy(currentLanguage = localeManager.currentLanguage) }
    }

    fun onStartGameClicked() {
        viewModelScope.launch {
            gameSessionRepository.update { if (it.isStarted) it else GameSession.newRun() }
            _uiEffect.send(StartUiEffect.NavigateToGame)
        }
    }

    fun onDifficultyClicked() {
        _uiState.update { it.copy(showDifficulty = true) }
    }

    fun onDifficultyDismissed() {
        _uiState.update { it.copy(showDifficulty = false) }
    }

    fun onEasyDifficultySelected() {
        viewModelScope.launch {
            gameSessionRepository.update { GameSession.instantLoss() }
            _uiEffect.send(StartUiEffect.NavigateToLoseResult)
        }
    }

    fun onHardDifficultySelected() {
        onDifficultyDismissed()
    }

    fun onLanguageSelected(language: AppLanguage) {
        _uiState.update { it.copy(currentLanguage = language) }
        localeManager.setLanguage(language)
    }

}
