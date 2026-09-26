package com.funnygaytest.platform.ads

import android.app.Activity
import android.content.Context
import com.funnygaytest.data.config.RemoteConfigRepository
import com.funnygaytest.di.ApplicationScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val adFrequencyManager: AdFrequencyManager,
    private val remoteConfigRepository: RemoteConfigRepository,
    @param:ApplicationScope private val scope: CoroutineScope
) {

    companion object {
        private const val AD_UNIT_ID = "R-M-19046095-1"
        // private const val AD_UNIT_ID = "demo-interstitial-applovin"
    }

    private val isInitializationRequested = MutableStateFlow(false)
    private var isActive = false
    private var isSdkInitialized = false
    private var interstitialAdLoader: InterstitialAdLoader? = null
    private var interstitialAd: InterstitialAd? = null

    init {
        scope.launch {
            combine(isInitializationRequested, remoteConfigRepository.adsEnabled) { requested, enabled ->
                requested && enabled == true
            }
                .distinctUntilChanged()
                .collect { active ->
                    isActive = active
                    if (active) start() else releaseAd()
                }
        }
    }

    fun initialize() {
        isInitializationRequested.value = true
    }

    fun showInterstitialIfAllowed(activity: Activity) {
        if (!isActive || !adFrequencyManager.canShowAd()) return
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

    private fun start() {
        if (isSdkInitialized) {
            if (interstitialAd == null) loadInterstitialAd()
            return
        }
        isSdkInitialized = true
        YandexAds.apply {
            setUserConsent(true)
            setAppAdAnalyticsReporting(true)
            initialize(context) {
                interstitialAdLoader = InterstitialAdLoader(context)
                if (isActive) loadInterstitialAd()
            }
        }
    }

    private fun loadInterstitialAd() {
        interstitialAdLoader?.loadAd(
            adRequest = AdRequest.Builder(AD_UNIT_ID).build(),
            listener = object : InterstitialAdLoadListener {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    if (isActive) this@AdsManager.interstitialAd = interstitialAd
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    Timber.e(error.description)
                }
            }
        )
    }

    private fun releaseAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    private fun releaseAndReload() {
        releaseAd()
        if (isActive) loadInterstitialAd()
    }
}
