package com.funnygaytest.domain.billing

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import com.funnygaytest.di.BillingModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class BillingInteractor @Inject constructor(
    private val billingClient: BillingClient,
    private val stateListener: MyBillingClientStateListener,
    private val purchasesUpdatedListener: MyPurchasesUpdatedListener,
    @Named(BillingModule.BILLING_SCOPE)
    private val billingScope: CoroutineScope,
    private val skuDetailsLoader: SkuDetailsLoader
) {
    var skuDetails = MutableLiveData<SkuDetails?>()
    val launchBillingFlow = billingClient::launchBillingFlow

    fun init() {
        purchasesUpdatedListener.consumeCallback = { consumeParams ->
            billingClient.consumeAsync(consumeParams) { _, _ -> }
        }
        billingClient.startConnection(stateListener)
        stateListener.loadDetailsCallback = {
            billingScope.launch {
                skuDetails.postValue(skuDetailsLoader.getProductDetails())
            }
        }
    }

    fun disconnect() {
        billingScope.cancel()
        billingClient.endConnection()
    }

}