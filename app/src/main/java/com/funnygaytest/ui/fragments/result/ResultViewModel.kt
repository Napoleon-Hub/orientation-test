package com.funnygaytest.ui.fragments.result

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.BillingFlowParams
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.billing.BillingInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val billingInteractor: BillingInteractor
) : ViewModel() {

    var skuDetails = billingInteractor.skuDetails

    fun launchBillingFlow(activity: Activity) {
        if (skuDetails.value == null) return
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(skuDetails.value!!)
            .build()
        billingInteractor.launchBillingFlow(activity, flowParams)
    }

    fun initBilling() = billingInteractor.init()

    fun refreshGameInfo() {
        gameBegun = false
        lastQuestion = 0
        points = 0
    }

    val isConnected: Boolean
        get() = preferences.isConnected

    var gameBegun: Boolean
        get() = preferences.gameBegun
        set(value) {
            preferences.gameBegun = value
        }

    var points: Int
        get() = preferences.points
        set(value) {
            preferences.points = value
        }

    private var lastQuestion: Int
        get() = preferences.lastQuestion
        set(value) {
            preferences.lastQuestion = value
        }

}