package com.funnygaytest.managers.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.funnygaytest.di.BillingModule
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class BillingInteractor @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @param:Named(BillingModule.BILLING_SCOPE) private val scope: CoroutineScope
) : PurchasesUpdatedListener {

    private val _productDetails = MutableStateFlow<ProductDetails?>(null)
    val productDetails = _productDetails.asStateFlow()

    private val productId = "com.funnygaytest.donate"

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    fun init() {
        if (!billingClient.isReady) {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        queryProductDetails()
                    }
                }

                override fun onBillingServiceDisconnected() {
                    // Здесь можно реализовать логику переподключения
                }
            })
        }
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
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                _productDetails.value = result.productDetailsList?.firstOrNull()
            }
        }
    }

    fun launchBillingFlow(activity: Activity, params: BillingFlowParams) {
        billingClient.launchBillingFlow(activity, params)
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            scope.launch {
                val result = billingClient.acknowledgePurchase(acknowledgePurchaseParams)
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Покупка успешно подтверждена!
                    // Здесь можно дернуть коллбек или обновить StateFlow, чтобы выдать пользователю награду
                }
            }
        }
    }
}