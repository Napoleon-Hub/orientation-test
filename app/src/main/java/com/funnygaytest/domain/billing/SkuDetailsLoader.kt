package com.funnygaytest.domain.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.querySkuDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SkuDetailsLoader @Inject constructor(private val billingClient: BillingClient) {

    @Throws(Exception::class)
    suspend fun getProductDetails(): SkuDetails? {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(skuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        val result = withContext(Dispatchers.IO) {
            billingClient.querySkuDetails(params)
        }
        val code = result.billingResult.responseCode
        if (code == BillingClient.BillingResponseCode.OK && result.skuDetailsList != null) {
            return result.skuDetailsList?.get(0)
        } else throw Exception("Unable getting sku details, code: $code")
    }

    companion object {
        val skuList = arrayListOf("com.funnygaytest.donate")
    }
}