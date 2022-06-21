package com.funnygaytest.domain.billing

import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.QueryProductDetailsParams
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

private const val DONATE_PRODUCT_ID = "com.funnygaytest.donate"

class ProductDetailsLoader @Inject constructor(private val billingClient: BillingClient) {

    var productDetails = MutableLiveData<ProductDetails>()

    fun getProductDetails() {
        val params = QueryProductDetailsParams.newBuilder().setProductList(
            listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(DONATE_PRODUCT_ID)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            )
        )
        billingClient.queryProductDetailsAsync(params.build()) { billingResult, productDetailsList ->
            val code = billingResult.responseCode
            if (code == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                productDetails.postValue(productDetailsList[0])
            } else {
                FirebaseCrashlytics.getInstance().log("Unable getting sku details, code: $code")
            }
        }
    }

}