package com.funnygaytest.ui.screens.connection

import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class NoInternetUiState(
    val isMuted: Boolean = false
)

@HiltViewModel
class NoInternetViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(NoInternetUiState(isMuted = isMuted))
    val uiState = _uiState.asStateFlow()

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}