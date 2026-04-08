package com.funnygaytest.ui.screens.result

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.managers.billing.BillingInteractor
import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.prefs.PrefsEntity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.testing.FakeReviewManager
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
    val points: Int = 0,
    val isMuted: Boolean = false,
    val isRateEnabled: Boolean = true
)

sealed class ResultUiEffect {
    object NavigateToStartScreen : ResultUiEffect()
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    preferences: PrefsEntity,
    audioManager: AudioManager,
    private val billingInteractor: BillingInteractor,
    @param:ApplicationContext private val appContext: Context
) : BaseViewModel(preferences, audioManager) {

    private val _uiState = MutableStateFlow(
        ResultUiState(
            points = points,
            isMuted = isMuted
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
    }

    fun onRestartClicked() {
        if (isConnected) {
            viewModelScope.launch { _uiEffect.emit(ResultUiEffect.NavigateToStartScreen) }
        }
    }

    fun getReviewInfo() {
        reviewManager = FakeReviewManager(appContext)
        //reviewManager = ReviewManagerFactory.create(appContext)
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

    fun refreshGameData() {
        gameBegun = false
        lastQuestionIndex = 0
    }

    override fun toggleMusic() {
        _uiState.update { it.copy(isMuted = !it.isMuted) }
        super.toggleMusic()
    }

}