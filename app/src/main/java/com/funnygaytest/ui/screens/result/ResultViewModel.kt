package com.funnygaytest.ui.screens.result

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.billing.BillingInteractor
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.utils.enums.EndingType
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

sealed class ResultUiEffect {
    object NavigateToStartScreen : ResultUiEffect()
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    audioManager: AudioManager,
    private val billingInteractor: BillingInteractor,
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

    private val _uiEffect = MutableSharedFlow<ResultUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    private val productDetails = billingInteractor.productDetails

    private var reviewManager: ReviewManager? = null
    private var reviewInfo: ReviewInfo? = null

    init {
        billingInteractor.init()
        processCurrentEnding()
        refreshGameData()
    }

    fun onRestartClicked() {
        viewModelScope.launch { _uiEffect.emit(ResultUiEffect.NavigateToStartScreen) }
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

    fun launchBillingFlow(activity: Activity) {
        productDetails.value?.let { details ->
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
            )
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            billingInteractor.launchBillingFlow(activity, billingFlowParams)
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

        val isNew = saveAndCheckIfNew(endingType)
        checkAllEndingsUnlocked()

        _uiState.update {
            it.copy(
                currentEnding = endingType,
                isNewEnding = isNew
            )
        }
    }

    private fun saveAndCheckIfNew(ending: EndingType): Boolean {
        var wasNew = false
        when (ending) {
            EndingType.WIN_100 -> if (preferences.endingWin100 == 0) { preferences.endingWin100 = 1; wasNew = true }
            EndingType.WIN_66 -> if (preferences.endingWin66 == 0) { preferences.endingWin66 = 1; wasNew = true }
            EndingType.WIN_33 -> if (preferences.endingWin33 == 0) { preferences.endingWin33 = 1; wasNew = true }
            EndingType.WIN_1 -> if (preferences.endingWin1 == 0) { preferences.endingWin1 = 1; wasNew = true }
            EndingType.LOSE_4 -> if (preferences.endingLose4 == 0) { preferences.endingLose4 = 1; wasNew = true }
            EndingType.LOSE_8 -> if (preferences.endingLose8 == 0) { preferences.endingLose8 = 1; wasNew = true }
            EndingType.LOSE_12 -> if (preferences.endingLose12 == 0) { preferences.endingLose12 = 1; wasNew = true }
            EndingType.LOSE_16 -> if (preferences.endingLose16 == 0) { preferences.endingLose16 = 1; wasNew = true }
            EndingType.LOSE_20 -> if (preferences.endingLose20 == 0) { preferences.endingLose20 = 1; wasNew = true }
            EndingType.LOSE_PUSSY -> if (preferences.endingLosePussy == 0) { preferences.endingLosePussy = 1; wasNew = true }
            EndingType.ALL -> {}
        }
        return wasNew
    }

    private fun checkAllEndingsUnlocked() {
        if (preferences.endingAll == 0) {
            val allUnlocked = preferences.endingWin100 > 0 && preferences.endingWin66 > 0 &&
                    preferences.endingWin33 > 0 && preferences.endingWin1 > 0 &&
                    preferences.endingLose4 > 0 && preferences.endingLose8 > 0 &&
                    preferences.endingLose12 > 0 && preferences.endingLose16 > 0 &&
                    preferences.endingLose20 > 0 && preferences.endingLosePussy > 0

            if (allUnlocked) {
                preferences.endingAll = 1
                _uiState.update {
                    it.copy(isAllEndingsUnlocked = true)
                }
            }
        }
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

    private fun refreshGameData() {
        if (health > 0) countOfWins += 1
        else countOfLoses += 1

        gameBegun = false
        lastQuestionIndex = 0
        health = 100
        currentQuestionList = listOf()
    }

}