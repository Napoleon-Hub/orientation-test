package com.funnygaytest.ui.screens.feed

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.managers.billing.BillingInteractor
import com.funnygaytest.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val isDonated: Boolean = false,
    val countOfProduct: Int = 0,
    val isDonateAchieveUnlocked: Boolean = false
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val billingInteractor: BillingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private val productDetails = billingInteractor.productDetails

    init {
        billingInteractor.init()

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
        _uiState.update {
            it.copy(
                isDonated = true,
                countOfProduct = it.countOfProduct + quantity,
                isDonateAchieveUnlocked = preferences.endingDonate == 0
            )
        }
        if (preferences.endingDonate == 0) {
            preferences.endingDonate = 1
        }
    }

}