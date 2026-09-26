package com.funnygaytest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.data.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MainUiState(
    val isLoading: Boolean = true,
    val consentShown: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<MainUiState> = settingsRepository.consentShown
        .map { MainUiState(isLoading = false, consentShown = it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState())

    fun onConsentAccepted() {
        viewModelScope.launch { settingsRepository.setConsentShown() }
    }

}
