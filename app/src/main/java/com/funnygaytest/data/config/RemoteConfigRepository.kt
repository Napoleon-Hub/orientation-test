package com.funnygaytest.data.config

import com.funnygaytest.BuildConfig
import com.funnygaytest.di.ApplicationScope
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigRepository @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    @param:ApplicationScope private val scope: CoroutineScope
) {

    private val _adsEnabled = MutableStateFlow<Boolean?>(null)
    val adsEnabled: StateFlow<Boolean?> = _adsEnabled.asStateFlow()

    init {
        scope.launch {
            remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
                minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else DEFAULT_FETCH_INTERVAL_SECONDS
            }).await()
            remoteConfig.setDefaultsAsync(mapOf(KEY_ADS_ENABLED to DEFAULT_ADS_ENABLED)).await()
            publishValues()
            try {
                remoteConfig.fetchAndActivate().await()
                publishValues()
            } catch (e: FirebaseRemoteConfigException) {
                Timber.w(e, "Remote config fetch failed, cached values are used")
            }
            listenForUpdates()
        }
    }

    private fun listenForUpdates() {
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                remoteConfig.activate().addOnCompleteListener { publishValues() }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Timber.w(error, "Remote config real-time updates failed")
            }
        })
    }

    private fun publishValues() {
        _adsEnabled.value = remoteConfig.getBoolean(KEY_ADS_ENABLED)
        Timber.d("Remote config: ads_enabled=${_adsEnabled.value}")
    }

    private companion object {
        const val KEY_ADS_ENABLED = "ads_enabled"
        const val DEFAULT_ADS_ENABLED = true
        const val DEFAULT_FETCH_INTERVAL_SECONDS = 3600L
    }
}
