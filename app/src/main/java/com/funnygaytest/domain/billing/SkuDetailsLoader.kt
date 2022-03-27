package com.funnygaytest.domain.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.querySkuDetails
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SkuDetailsLoader @Inject constructor(private val billingClient: BillingClient) {

    suspend fun getProductDetails(): SkuDetails? {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val result = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params)
        }
        val code = result.billingResult.responseCode
        return if (code == BillingClient.BillingResponseCode.OK && result.skuDetailsList != null
            && result.skuDetailsList!!.isNotEmpty()) {
            result.skuDetailsList?.get(0)
        } else {
            FirebaseCrashlytics.getInstance().log("Unable getting sku details, code: $code")
            null
        }
    }

    companion object {
        val skuList = arrayListOf("com.funnygaytest.donate")
    }
}