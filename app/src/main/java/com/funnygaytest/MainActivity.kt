package com.funnygaytest

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA
import dagger.hilt.android.AndroidEntryPoint

import com.google.android.ump.ConsentInformation

import com.google.android.ump.UserMessagingPlatform

import com.google.android.ump.ConsentRequestParameters
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var consentInformation: ConsentInformation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addNetworkCallback()
        MobileAds.initialize(this)

        ConsentDebugSettings.Builder(this).setDebugGeography(DEBUG_GEOGRAPHY_EEA)

        // GDPR
        val params = ConsentRequestParameters.Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation?.requestConsentInfoUpdate(
            this,
            params,
            { if (consentInformation!!.isConsentFormAvailable) loadConsentForm() },
            {}
        )

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

    private fun loadConsentForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { form ->
                if (consentInformation!!.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    form.show(this@MainActivity) { loadConsentForm() }
                }
            }
        ) {}
    }
}