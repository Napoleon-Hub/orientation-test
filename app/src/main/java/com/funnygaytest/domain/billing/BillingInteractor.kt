package com.funnygaytest.domain.billing

import com.android.billingclient.api.BillingClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingInteractor @Inject constructor(
    private val billingClient: BillingClient,
    private val stateListener: MyBillingClientStateListener,
    private val purchasesUpdatedListener: MyPurchasesUpdatedListener,
    private val productDetailsLoader: ProductDetailsLoader
) {
    var productDetails = productDetailsLoader.productDetails
    val launchBillingFlow = billingClient::launchBillingFlow

    fun init() {
        purchasesUpdatedListener.consumeCallback = { consumeParams ->
            billingClient.consumeAsync(consumeParams) { _, _ -> }
        }
        billingClient.startConnection(stateListener)
        stateListener.loadDetailsCallback = {
            productDetailsLoader.getProductDetails()
        }
    }
}