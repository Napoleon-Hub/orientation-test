package com.funnygaytest

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.funnygaytest.managers.ads.AdsManager
import com.funnygaytest.managers.firebase.firestore.FirestoreManager
import com.funnygaytest.managers.network.NetworkMonitor
import com.funnygaytest.navigation.AppNavigation
import com.funnygaytest.ui.components.dialogs.LaboratoryAccessDialog
import com.funnygaytest.ui.screens.connection.NoInternetScreen
import com.funnygaytest.ui.theme.LabTheme
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var firestoreManager: FirestoreManager

    @Inject
    lateinit var adsManager: AdsManager

    private val viewModel: MainViewModel by viewModels()

    private var appUpdateManager: AppUpdateManager? = null
    private var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemUI()

        firestoreManager.authorizeFirebase(this)
        setupAppUpdate()

        setContent {
            LabTheme {
                val isConnected by networkMonitor.isConnected.collectAsState(initial = true)
                val uiState by viewModel.uiState.collectAsState()

                val context = LocalContext.current
                val activity = context as? Activity

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        onRequestShowAd = { adsManager.showInterstitialIfAllowed(this@MainActivity) }
                    )

                    if (!isConnected) {
                        NoInternetScreen()
                    } else if (!uiState.consentShown) {
                        LaboratoryAccessDialog(
                            onConsentAccepted = {
                                viewModel.updateConsentState()
                            },
                            onDecline = {
                                activity?.finish()
                            }
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