package com.funnygaytest.ui.screens.endings

import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.EndingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
    audioManager: AudioManager,
    private val firestoreManager: FirestoreManager
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(EndingsUiState(isMuted = isMuted))
    val uiState = _uiState.asStateFlow()

    init {
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
                        wins = currentStats.wins,
                        loses = currentStats.losses,
                        endings = listOf(
                            EndingItemState(EndingType.WIN_100, achievements.contains(EndingType.WIN_100.id)),
                            EndingItemState(EndingType.WIN_66, achievements.contains(EndingType.WIN_66.id)),
                            EndingItemState(EndingType.WIN_33, achievements.contains(EndingType.WIN_33.id)),
                            EndingItemState(EndingType.WIN_1, achievements.contains(EndingType.WIN_1.id)),
                            EndingItemState(EndingType.LOSE_4, achievements.contains(EndingType.LOSE_4.id)),
                            EndingItemState(EndingType.LOSE_8, achievements.contains(EndingType.LOSE_8.id)),
                            EndingItemState(EndingType.LOSE_12, achievements.contains(EndingType.LOSE_12.id)),
                            EndingItemState(EndingType.LOSE_16, achievements.contains(EndingType.LOSE_16.id)),
                            EndingItemState(EndingType.LOSE_20, achievements.contains(EndingType.LOSE_20.id)),
                            EndingItemState(EndingType.LOSE_PUSSY, achievements.contains(EndingType.LOSE_PUSSY.id)),
                            EndingItemState(EndingType.ALL, achievements.contains(EndingType.ALL.id)),
                            EndingItemState(EndingType.DONATE, achievements.contains(EndingType.DONATE.id))
                        )
                    )
                }
            }
        }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}