package com.funnygaytest.ui.screens.feed

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.data.billing.BillingRepository
import com.funnygaytest.data.stats.StatsRepository
import com.funnygaytest.model.EndingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FeedUiState(
    val isDonated: Boolean = false,
    val isDonateAchieveUnlocked: Boolean = false
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private val productDetails = billingRepository.productDetails

    private var currentAchievements: List<String> = emptyList()

    init {
        billingRepository.init()

        viewModelScope.launch {
            statsRepository.observeStats().collect { currentAchievements = it.achievements }
        }

        viewModelScope.launch {
            billingRepository.purchaseEvent.collect {
                handleSuccessfulDonation()
            }
        }
    }

    fun launchBillingFlow(activity: Activity) {
        val details = productDetails.value
        if (details == null) {
            Timber.w("Product details not loaded yet, retrying")
            billingRepository.init()
            return
        }
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        billingRepository.launchBillingFlow(activity, billingFlowParams)
    }

    private fun handleSuccessfulDonation() {
        _uiState.update { it.copy(isDonated = true) }
        if (EndingType.DONATE.id in currentAchievements) return
        viewModelScope.launch {
            val recorded = statsRepository.recordTestResult(achievementIds = listOf(EndingType.DONATE.id))
            _uiState.update { it.copy(isDonateAchieveUnlocked = recorded) }
        }
    }

}
