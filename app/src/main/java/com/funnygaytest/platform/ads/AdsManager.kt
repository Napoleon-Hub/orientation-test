package com.funnygaytest.platform.ads

import android.app.Activity
import android.content.Context
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.YandexAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val adFrequencyManager: AdFrequencyManager
) {

    companion object {
        private const val AD_UNIT_ID = "R-M-19046095-1"
        // private const val AD_UNIT_ID = "demo-interstitial-applovin"
    }

    private var isInitialized = false
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var interstitialAd: InterstitialAd? = null

    fun initialize() {
        if (isInitialized) return
        isInitialized = true
        YandexAds.apply {
            setUserConsent(true)
            setAppAdAnalyticsReporting(true)
            initialize(context) {
                interstitialAdLoader = InterstitialAdLoader(context)
                loadInterstitialAd()
            }
        }
    }

    fun showInterstitialIfAllowed(activity: Activity) {
        if (!adFrequencyManager.canShowAd()) return
        val ad = interstitialAd ?: return

        ad.setAdEventListener(object : InterstitialAdEventListener {
            override fun onAdShown() {
                adFrequencyManager.recordAdShown()
            }

            override fun onAdFailedToShow(adError: AdError) {
                Timber.w(adError.description)
                releaseAndReload()
            }

            override fun onAdDismissed() {
                releaseAndReload()
            }

            override fun onAdClicked() = Unit

            override fun onAdImpression(impressionData: ImpressionData?) = Unit
        })
        ad.show(activity)
    }

    private fun loadInterstitialAd() {
        interstitialAdLoader?.loadAd(
            adRequest = AdRequest.Builder(AD_UNIT_ID).build(),
            listener = object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@AdsManager.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Timber.e(error.description)
                }
            }
        )
    }

    private fun releaseAndReload() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
        loadInterstitialAd()
    }
}
