package com.funnygaytest.domain.billing

import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.OK
import com.funnygaytest.di.BillingModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MyPurchasesUpdatedListener @Inject constructor() : PurchasesUpdatedListener {

    var consumeCallback: (consumeParams: ConsumeParams) -> Unit = {}

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        if (result.responseCode == OK && purchases != null) {
            purchases.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    val consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    consumeCallback(consumeParams)
                }
            }
        }
    }
}

@Singleton
class MyBillingClientStateListener @Inject constructor(
    @Named(BillingModule.BILLING_SCOPE)
    private val billingScope: CoroutineScope,
    private val billingClient: BillingClient
) : BillingClientStateListener {
    var loadDetailsCallback: () -> Unit = {}

    override fun onBillingSetupFinished(result: BillingResult) {
        retryCount = 0
        if (result.responseCode == OK) loadDetailsCallback()
    }

    override fun onBillingServiceDisconnected() {
        billingScope.launch {
            delay(reconnectDelay * retryCount)
            billingClient.startConnection(this@MyBillingClientStateListener)
        }
    }

    private var retryCount = 0
    private val reconnectDelay = 3000L
}
