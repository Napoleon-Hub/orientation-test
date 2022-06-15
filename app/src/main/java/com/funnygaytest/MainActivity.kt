package com.funnygaytest

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.funnygaytest.screens.game.GameScreen
import com.funnygaytest.screens.game.GameViewModel
import com.funnygaytest.screens.result.ResultScreen
import com.funnygaytest.screens.result.ResultViewModel
import com.funnygaytest.screens.start.StartScreen
import com.funnygaytest.screens.start.StartViewModel
import com.funnygaytest.ui.themes.MainTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val START_SCREEN_NAME: String = "start"
const val GAME_SCREEN_NAME: String = "game"
const val RESULT_SCREEN_NAME: String = "result"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var interstitialAd: InterstitialAd? = null
    private lateinit var fullScreenContentCallback: FullScreenContentCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addNetworkCallback()
        MobileAds.initialize(this)
        checkRequestConsentInfoUpdate()

        fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
            }
        }
        loadVideoAd()

        setContent {
            MainTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = START_SCREEN_NAME) {
                    composable(START_SCREEN_NAME) {
                        val startViewModel = hiltViewModel<StartViewModel>()
                        StartScreen(
                            screenOrientation = resources.configuration.orientation,
                            navController = navController,
                            viewModel = startViewModel
                        )
                    }

                    composable(GAME_SCREEN_NAME) {
                        val gameViewModel = hiltViewModel<GameViewModel>()
                        GameScreen(
                            screenOrientation = resources.configuration.orientation,
                            navController = navController,
                            viewModel = gameViewModel
                        )
                    }

                    composable(RESULT_SCREEN_NAME) {
                        if (interstitialAd != null) interstitialAd?.show(this@MainActivity)
                        val resultViewModel = hiltViewModel<ResultViewModel>()
                        ResultScreen(
                            screenOrientation = resources.configuration.orientation,
                            navController = navController,
                            viewModel = resultViewModel
                        )
                    }
                }

            }
        }
    }

    private fun addNetworkCallback() {
        val cm: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder: NetworkRequest.Builder = NetworkRequest.Builder()
        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    runOnUiThread {
                        viewModel.setConnection(true)
                    }
                }

                override fun onLost(network: Network) {
                    runOnUiThread {
                        viewModel.setConnection(false)
                    }
                }
            }
        )
    }

    // GDPR
    private fun checkRequestConsentInfoUpdate() {
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            { if (consentInformation.isConsentFormAvailable) loadConsentForm(consentInformation) },
            {}
        )
    }

    private fun loadConsentForm(consentInformation: ConsentInformation) {
        UserMessagingPlatform.loadConsentForm(
            this,
            { form ->
                if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(this@MainActivity) { loadConsentForm(consentInformation) }
                }
            }
        ) {}
    }

    private fun loadVideoAd() {
        CoroutineScope(Dispatchers.IO).launch {
            val adRequest: AdRequest = AdRequest.Builder().build()
            withContext(Dispatchers.Main) {
                InterstitialAd.load(
                    this@MainActivity,
                    this@MainActivity.getString(R.string.video_ad_unit_id),
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            interstitialAd = ad
                            interstitialAd!!.fullScreenContentCallback = fullScreenContentCallback
                        }
                    })
            }
        }
    }
}