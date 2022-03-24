package com.funnygaytest.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.funnygaytest.domain.billing.MyPurchasesUpdatedListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBilling(
        @ApplicationContext context: Context,
        purchaseUpdatedListener: MyPurchasesUpdatedListener
    ): BillingClient {
        return BillingClient.newBuilder(context)
            .setListener(purchaseUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    @Provides
    @Singleton
    @Named(BILLING_SCOPE)
    fun provideScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    const val BILLING_SCOPE = "BillingScope"
}