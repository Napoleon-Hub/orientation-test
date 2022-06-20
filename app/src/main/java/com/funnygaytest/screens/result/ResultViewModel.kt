package com.funnygaytest.screens.result

import android.app.Activity
import androidx.lifecycle.LiveData
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.billing.BillingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    preferences: PrefsEntity,
    private val billingInteractor: BillingInteractor
) : BaseViewModel<ResultContract.Event, ResultContract.State, ResultContract.Effect>(preferences) {

    val skuDetails: LiveData<SkuDetails?> = billingInteractor.skuDetails

    init {
        billingInteractor.init()
    }

    override fun createInitialState(): ResultContract.State =
        ResultContract.State.ViewStateGameResult

    override fun handleEvent(event: ResultContract.Event) {
        when (event) {
            ResultContract.Event.OnShareResultsClick -> {
                setEffect { ResultContract.Effect.OpenShareActivity }
            }
            is ResultContract.Event.OnPayClick -> {
                launchBillingFlow(event.activity)
            }
            ResultContract.Event.OnRateUsClick -> {
                setEffect { ResultContract.Effect.OpenRateUsActivity }
            }
            ResultContract.Event.OnRestartGameClick -> {
                if (isConnected) {
                    setEffect { ResultContract.Effect.NavigateToStartScreen }
                } else {
                    setEffect { ResultContract.Effect.ShowConnectionErrorDialog }
                }
            }
            ResultContract.Event.OnAnotherAppsClick -> {
                setEffect { ResultContract.Effect.OpenAnotherAppsActivity }
            }
        }
    }

    private fun launchBillingFlow(activity: Activity) {
        if (skuDetails.value == null) return
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails.value!!)
            .build()
        billingInteractor.launchBillingFlow(activity, flowParams)
    }

    fun refreshGameData() {
        gameBegun = false
        lastQuestionIndex = 0
    }

}