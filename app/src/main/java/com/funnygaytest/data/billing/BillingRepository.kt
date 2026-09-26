package com.funnygaytest.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.funnygaytest.di.BillingModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class BillingRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named(BillingModule.BILLING_SCOPE) private val scope: CoroutineScope
) : PurchasesUpdatedListener {

    private val _purchaseEvent = MutableSharedFlow<Int>()
    val purchaseEvent = _purchaseEvent.asSharedFlow()

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails = _productDetails.asStateFlow()

    private val productId = "com.funnygaytest.donate"

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .enableAutoServiceReconnection()
        .build()

    fun init() {
        if (billingClient.isReady) {
            if (_productDetails.value == null) queryProductDetails()
            processUnconsumedPurchases()
            return
        }
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    processUnconsumedPurchases()
                } else {
                    Timber.e("Billing setup failed: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Timber.w("GBPL Service disconnected")
            }
        })
    }

    private fun queryProductDetails() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        scope.launch {
            val result = billingClient.queryProductDetails(params)
            val details = result.productDetailsList?.firstOrNull()
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK && details != null) {
                _productDetails.value = details
            } else {
                Timber.e("Query product details failed: ${result.billingResult.responseCode} ${result.billingResult.debugMessage}")
            }
        }
    }

    private fun processUnconsumedPurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        scope.launch {
            val result = billingClient.queryPurchasesAsync(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                result.purchasesList.forEach { handlePurchase(it) }
            } else {
                Timber.e("Query purchases failed: ${result.billingResult.debugMessage}")
            }
        }
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        val result = billingClient.launchBillingFlow(activity, params)
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> Unit
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> processUnconsumedPurchases()
            else -> Timber.e("Launch billing flow failed: ${result.responseCode} ${result.debugMessage}")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> purchases?.forEach { handlePurchase(it) }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> processUnconsumedPurchases()
            else -> Timber.w("Purchase not completed: ${billingResult.responseCode} ${billingResult.debugMessage}")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {

            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            scope.launch {
                val consumeResult = billingClient.consumePurchase(consumeParams)
                if (consumeResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _purchaseEvent.emit(purchase.quantity)
                } else {
                    Timber.e("Error consume purchase: ${consumeResult.billingResult.debugMessage}")
                }
            }
        }
    }

}
