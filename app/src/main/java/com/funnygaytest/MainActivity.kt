package com.funnygaytest

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.funnygaytest.managers.ads.AdFrequencyManager
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.network.NetworkMonitor
import com.funnygaytest.navigation.AppNavigation
import com.funnygaytest.ui.components.dialogs.LaboratoryAccessDialog
import com.funnygaytest.ui.screens.connection.NoInternetScreen
import com.funnygaytest.ui.themes.MainTheme
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.yandex.mobile.ads.common.AdError
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val AD_UNIT_ID = "R-M-19046095-1"
    }

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var firestoreManager: FirestoreManager

    @Inject
    lateinit var adFrequencyManager: AdFrequencyManager

    private val viewModel: MainViewModel by viewModels()

    private var interstitialAd: InterstitialAd? = null
    private var interstitialAdLoader: InterstitialAdLoader? = null

    private var appUpdateManager: AppUpdateManager? = null
    private var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()

        firestoreManager.authorizeFirebase(this)
        setupAppUpdate()

        setContent {
            MainTheme {
                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)
                val uiState by viewModel.uiState.collectAsState()

                val context = LocalContext.current
                val activity = context as? Activity

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        onRequestShowAd = {
                            if (adFrequencyManager.canShowAd()) {
                                showAd()
                                adFrequencyManager.recordAdShown()
                            }
                        }
                    )

                    if (!isConnected) {
                        NoInternetScreen()
                    } else {
                        if (!uiState.consentShown) {
                            LaboratoryAccessDialog(
                                onConsentAccepted = {
                                    viewModel.updateConsentState()
                                    initializeMobileAds()
                                },
                                onDecline = {
                                    activity?.finish()
                                }
                            )
                        } else {
                            initializeMobileAds()
                        }
                    }
                }
            }
        }
    }

    private fun setupAppUpdate() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { _ -> }
        checkAppUpdatesAvailable()
    }

    private fun checkAppUpdatesAvailable() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager?.appUpdateInfo

        appUpdateInfoTask?.addOnSuccessListener { appUpdateInfo: AppUpdateInfo ->
            try {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager?.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher!!,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            } catch (_: Exception) {
            }
        }

    }

    private fun initializeMobileAds() {
        MobileAds.apply {
            setUserConsent(true)
            setAppAdAnalyticsReporting(true)
            initialize(this@MainActivity) {
                interstitialAdLoader = InterstitialAdLoader(this@MainActivity).apply {
                    setAdLoadListener(object : InterstitialAdLoadListener {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            this@MainActivity.interstitialAd = interstitialAd
                        }

                        override fun onAdFailedToLoad(error: AdRequestError) {
                            // Ad failed to load with AdRequestError.
                            // Attempting to load a new ad from the onAdFailedToLoad() method is strongly discouraged.
                        }
                    })
                }
                loadInterstitialAd()
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequestConfiguration = AdRequestConfiguration.Builder(AD_UNIT_ID).build()
        interstitialAdLoader?.loadAd(adRequestConfiguration)
    }

    private fun showAd() {
        interstitialAd?.apply {
            setAdEventListener(object : InterstitialAdEventListener {
                override fun onAdShown() {
                    // Called when ad is shown.
                }
                override fun onAdFailedToShow(adError: AdError) {
                    // Called when an InterstitialAd failed to show.
                    // Clean resources after Ad dismissed
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }
                override fun onAdDismissed() {
                    // Called when ad is dismissed.
                    // Clean resources after Ad dismissed
                    interstitialAd?.setAdEventListener(null)
                    interstitialAd = null

                    // Now you can preload the next interstitial ad.
                    loadInterstitialAd()
                }
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                }
                override fun onAdImpression(impressionData: ImpressionData?) {
                    // Called when an impression is recorded for an ad.
                }
            })
            show(this@MainActivity)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interstitialAdLoader?.setAdLoadListener(null)
        interstitialAdLoader = null
        destroyInterstitialAd()
    }

    private fun destroyInterstitialAd() {
        interstitialAd?.setAdEventListener(null)
        interstitialAd = null
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    private fun hideSystemUI() {
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

}