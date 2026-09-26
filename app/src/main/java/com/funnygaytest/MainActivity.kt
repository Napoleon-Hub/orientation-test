package com.funnygaytest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.funnygaytest.data.stats.StatsRepository
import com.funnygaytest.navigation.AppNavigation
import com.funnygaytest.platform.ads.AdsManager
import com.funnygaytest.platform.network.NetworkMonitor
import com.funnygaytest.ui.components.dialogs.LaboratoryAccessDialog
import com.funnygaytest.ui.screens.connection.NoInternetScreen
import com.funnygaytest.ui.theme.LabTheme
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var statsRepository: StatsRepository

    @Inject
    lateinit var adsManager: AdsManager

    private val viewModel: MainViewModel by viewModels()

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    private val appUpdateLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == ActivityResult.RESULT_IN_APP_UPDATE_FAILED) Timber.w("In-app update failed")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition { viewModel.uiState.value.isLoading }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        hideSystemBars()

        if (savedInstanceState == null) {
            lifecycleScope.launch { statsRepository.signInWithPlayGames(this@MainActivity) }
            checkForAppUpdate()
        }

        setContent {
            LabTheme {
                val isConnected by networkMonitor.isConnected.collectAsStateWithLifecycle(initialValue = true)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        onRequestShowAd = { adsManager.showInterstitialIfAllowed(this@MainActivity) }
                    )

                    if (!isConnected) {
                        NoInternetScreen()
                    } else if (!uiState.isLoading && !uiState.consentShown) {
                        LaboratoryAccessDialog(
                            onConsentAccepted = viewModel::onConsentAccepted,
                            onDecline = ::finish
                        )
                    }
                }

                val canInitAds = isConnected && uiState.consentShown
                LaunchedEffect(canInitAds) {
                    if (canInitAds) adsManager.initialize()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resumeStalledAppUpdate()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    private fun hideSystemBars() {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    startImmediateUpdate(info)
                }
            }
            .addOnFailureListener { Timber.w(it, "Failed to check for app update") }
    }

    private fun resumeStalledAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdate(info)
            }
        }
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) {
        runCatching {
            appUpdateManager.startUpdateFlowForResult(
                info,
                appUpdateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
            )
        }.onFailure { Timber.w(it, "Failed to start app update flow") }
    }
}
