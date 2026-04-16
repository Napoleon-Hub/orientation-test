package com.funnygaytest.ui.screens.result

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.models.firebase.LabStats
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.EndingType
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val isMuted: Boolean = false,
    val isRateEnabled: Boolean = true,
    val healthLeft: Int = 0,
    val lastQuestionNumber: Int = 0,
    val currentEnding: EndingType? = null,
    val isNewEnding: Boolean = false,
    val isAllEndingsUnlocked: Boolean = false
)

@HiltViewModel
class ResultViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager,
    private val firestoreManager: FirestoreManager,
    @param:ApplicationContext private val appContext: Context
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(
        ResultUiState(
            isMuted = isMuted,
            healthLeft = health,
            lastQuestionNumber = lastQuestionIndex + 1
        )
    )
    val uiState = _uiState.asStateFlow()

    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    init {
        processCurrentEnding()
        refreshGameData()
    }

    fun getReviewInfo() {
        reviewManager = ReviewManagerFactory.create(appContext)
        val request = reviewManager?.requestReviewFlow()
        request?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _uiState.update { it.copy(isRateEnabled = true) }
                reviewInfo = task.result
            } else {
                _uiState.update { it.copy(isRateEnabled = false) }
                // @ReviewErrorCode val reviewErrorCode = (task.exception as ReviewException).errorCode
            }
        }
    }

    fun rateUs(activity: Activity) {
        reviewInfo?.let { reviewInfo ->
            val flow = reviewManager?.launchReviewFlow(activity, reviewInfo)
            flow?.addOnCompleteListener {
                _uiState.update { it.copy(isRateEnabled = false) }
            }
        }
    }

    private fun processCurrentEnding() {
        val qNum = lastQuestionIndex + 1
        val hp = health

        val endingType = when {
            hp > 0 -> {
                when (hp) {
                    100 -> EndingType.WIN_100
                    in 66..99 -> EndingType.WIN_66
                    in 33..65 -> EndingType.WIN_33
                    else -> EndingType.WIN_1
                }
            }
            else -> {
                when (qNum) {
                    in 2..7 -> EndingType.LOSE_4
                    in 8..11 -> EndingType.LOSE_8
                    in 12..15 -> EndingType.LOSE_12
                    in 16..19 -> EndingType.LOSE_16
                    20 -> EndingType.LOSE_20
                    else -> EndingType.LOSE_PUSSY
                }
            }
        }

        viewModelScope.launch {
            val currentStats = firestoreManager.getStats().firstOrNull() ?: LabStats()

            val isNew = !currentStats.achievements.contains(endingType.id)

            firestoreManager.recordTestResult(isWin = hp > 0, achievementId = endingType.id)

            val allUnlockedAchievements = currentStats.achievements + endingType.id
            val uniqueCoreEndings = allUnlockedAchievements.filter {
                it != EndingType.ALL.id && it != EndingType.DONATE.id
            }.distinct().size

            var isAllUnlockedNow = currentStats.achievements.contains(EndingType.ALL.id)

            if (uniqueCoreEndings >= 10 && !isAllUnlockedNow) {
                firestoreManager.recordTestResult(achievementId = EndingType.ALL.id)
                isAllUnlockedNow = true
            }

            _uiState.update {
                it.copy(
                    currentEnding = endingType,
                    isNewEnding = isNew,
                    isAllEndingsUnlocked = isAllUnlockedNow
                )
            }
        }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

    private fun refreshGameData() {
        gameBegun = false
        lastQuestionIndex = 0
        health = 100
        currentQuestionList = listOf()
    }

}