package com.funnygaytest.ui.screens.endings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.data.stats.StatsRepository
import com.funnygaytest.model.EndingType
import com.funnygaytest.platform.audio.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EndingsUiState(
    val wins: Int = 0,
    val loses: Int = 0,
    val endings: List<DiagnosisItemState> = emptyList()
)

data class DiagnosisItemState(
    val type: EndingType,
    val isUnlocked: Boolean
)

@HiltViewModel
class EndingsViewModel @Inject constructor(
    musicController: MusicController,
    private val statsRepository: StatsRepository
) : ViewModel(), MusicController by musicController {

    private val _uiState = MutableStateFlow(EndingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            statsRepository.observeStats().collect { stats ->
                _uiState.update { state ->
                    state.copy(
                        wins = stats.wins,
                        loses = stats.losses,
                        endings = EndingType.entries.map { type ->
                            DiagnosisItemState(type, isUnlocked = type.id in stats.achievements)
                        }
                    )
                }
            }
        }
    }

}
