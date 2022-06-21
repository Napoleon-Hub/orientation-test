package com.wildlifesurvivaltest.screens.result

import android.app.Activity
import androidx.lifecycle.LiveData
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.billing.BillingInteractor
import com.funnygaytest.screens.result.ResultContract
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

private const val EVENT_NAME_RATE_US = "rate_us"
private const val EVENT_NAME_RESTART = "restart"
private const val EVENT_NAME_ANOTHER_APPS = "another_apps"

@HiltViewModel
class ResultViewModel @Inject constructor(
    preferences: PrefsEntity,
    private val firebaseAnalytics: FirebaseAnalytics,
    private val billingInteractor: BillingInteractor
) : BaseViewModel<ResultContract.Event, ResultContract.State, ResultContract.Effect>(preferences) {

    val productDetails: LiveData<ProductDetails?> = billingInteractor.productDetails

    init {
        billingInteractor.init()
    }

    override fun createInitialState(): ResultContract.State =
        ResultContract.State.ViewStateGameResult

    override fun handleEvent(event: ResultContract.Event) {
        when (event) {
            ResultContract.Event.OnShareResultsClick -> {
                sendOptionClickEvent(FirebaseAnalytics.Event.SHARE)
                setEffect { ResultContract.Effect.OpenShareActivity }
            }
            is ResultContract.Event.OnPayClick -> {
                sendOptionClickEvent(FirebaseAnalytics.Event.PURCHASE)
                launchBillingFlow(event.activity)
            }
            ResultContract.Event.OnRateUsClick -> {
                sendOptionClickEvent(EVENT_NAME_RATE_US)
                setEffect { ResultContract.Effect.OpenRateUsActivity }
            }
            ResultContract.Event.OnRestartGameClick -> {
                if (isConnected) {
                    sendOptionClickEvent(EVENT_NAME_RESTART)
                    setEffect { ResultContract.Effect.NavigateToStartScreen }
                } else {
                    setEffect { ResultContract.Effect.ShowConnectionErrorDialog }
                }
            }
            ResultContract.Event.OnAnotherAppsClick -> {
                sendOptionClickEvent(EVENT_NAME_ANOTHER_APPS)
                setEffect { ResultContract.Effect.OpenAnotherAppsActivity }
            }
        }
    }

    private fun launchBillingFlow(activity: Activity) {
        if (productDetails.value != null) {
            val productDetailsParamsList =
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails.value!!)
                        .build()
                )
            val billingFlowParams =
                BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList).build()

            billingInteractor.launchBillingFlow(activity, billingFlowParams)
        } else return
    }

    private fun sendOptionClickEvent(eventName: String) {
        firebaseAnalytics.logEvent(eventName, null)
    }

    fun refreshGameData() {
        gameBegun = false
        lastQuestionIndex = 0
    }

}