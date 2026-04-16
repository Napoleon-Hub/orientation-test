package com.funnygaytest.ui.screens.feed

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.managers.billing.BillingInteractor
import com.funnygaytest.managers.firestore.FirestoreManager
import com.funnygaytest.utils.enums.EndingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FeedUiState(
    val isDonated: Boolean = false,
    val countOfProduct: Int = 0,
    val isDonateAchieveUnlocked: Boolean = false
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val billingInteractor: BillingInteractor,
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private val productDetails = billingInteractor.productDetails

    private var currentAchievements: List<String> = emptyList()

    init {
        billingInteractor.init()

        viewModelScope.launch {
            firestoreManager.getStats()
                .catch { e ->
                    Timber.e(e, "Ошибка доступа к личному делу исследователя")
                }
                .collect { stats ->
                currentAchievements = stats?.achievements ?: emptyList()
            }
        }

        viewModelScope.launch {
            billingInteractor.purchaseEvent.collect { quantity ->
                handleSuccessfulDonation(quantity)
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

    private fun handleSuccessfulDonation(quantity: Int) {
        val isAlreadyUnlocked = currentAchievements.contains(EndingType.DONATE.id)

        _uiState.update {
            it.copy(
                isDonated = true,
                countOfProduct = it.countOfProduct + quantity,
                isDonateAchieveUnlocked = !isAlreadyUnlocked
            )
        }
        if (!isAlreadyUnlocked) {
            firestoreManager.recordTestResult(achievementId = EndingType.DONATE.id)
        }
    }

}