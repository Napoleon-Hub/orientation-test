package com.funnygaytest.ui.screens.endings

import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.EndingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class EndingsUiState(
    val wins: Int = 0,
    val loses: Int = 0,
    val endings: List<EndingItemState> = emptyList(),
    val isMuted: Boolean = false
)

data class EndingItemState(
    val type: EndingType,
    val isUnlocked: Boolean
)

@HiltViewModel
class EndingsViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(EndingsUiState(
        wins = preferences.countOfWins,
        loses = preferences.countOfLoses,
        endings = listOf(
            EndingItemState(EndingType.WIN_100, preferences.endingWin100 > 0),
            EndingItemState(EndingType.WIN_66, preferences.endingWin66 > 0),
            EndingItemState(EndingType.WIN_33, preferences.endingWin33 > 0),
            EndingItemState(EndingType.WIN_1, preferences.endingWin1 > 0),
            EndingItemState(EndingType.LOSE_4, preferences.endingLose4 > 0),
            EndingItemState(EndingType.LOSE_8, preferences.endingLose8 > 0),
            EndingItemState(EndingType.LOSE_12, preferences.endingLose12 > 0),
            EndingItemState(EndingType.LOSE_16, preferences.endingLose16 > 0),
            EndingItemState(EndingType.LOSE_20, preferences.endingLose20 > 0),
            EndingItemState(EndingType.LOSE_PUSSY, preferences.endingLosePussy > 0),
            EndingItemState(EndingType.ALL, preferences.endingAll > 0)
        ),
        isMuted = isMuted
    ))
    val uiState = _uiState.asStateFlow()

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}