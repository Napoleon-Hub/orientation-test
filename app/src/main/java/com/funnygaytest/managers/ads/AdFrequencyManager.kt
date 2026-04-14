package com.funnygaytest.managers.ads

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdFrequencyManager @Inject constructor() {

    private var lastAdShowTime: Long = 0
    private val adIntervalMillis = 5 * 60 * 1000

    fun canShowAd(): Boolean {
        val currentTime = SystemClock.elapsedRealtime()
        return (currentTime - lastAdShowTime) >= adIntervalMillis
    }

    fun recordAdShown() {
        lastAdShowTime = SystemClock.elapsedRealtime()
    }
}