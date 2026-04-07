package com.funnygaytest.screens.result

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.billing.BillingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResultUiState(
    val points: Int = 0
)

sealed class ResultUiEffect {
    object NavigateToStartScreen : ResultUiEffect()
    object ShowConnectionErrorDialog : ResultUiEffect()
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val billingInteractor: BillingInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultUiState(points = preferences.points))
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<ResultUiEffect>()
    val uiEffect = _uiEffect.asSharedFlow()

    val productDetails = billingInteractor.productDetails

    init {
        billingInteractor.init()
    }

    fun onRestartClicked() {
        if (preferences.isConnected) {
            refreshGameData()
            viewModelScope.launch { _uiEffect.emit(ResultUiEffect.NavigateToStartScreen) }
        } else {
            viewModelScope.launch { _uiEffect.emit(ResultUiEffect.ShowConnectionErrorDialog) }
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
                .setProductDetailsParamsList(productDetailsParamsList).build()

            billingInteractor.launchBillingFlow(activity, billingFlowParams)
        }
    }

    private fun refreshGameData() {
        preferences.gameBegun = false
        preferences.lastQuestionIndex = 0
        preferences.points = 0
    }
}