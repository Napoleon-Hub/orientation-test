package com.funnygaytest

import android.app.Application
import com.funnygaytest.platform.appcheck.appCheckProviderFactory
import com.funnygaytest.platform.logging.ReleaseTree
import com.google.android.gms.games.PlayGamesSdk
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Firebase.appCheck.installAppCheckProviderFactory(appCheckProviderFactory())
        PlayGamesSdk.initialize(this)
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else ReleaseTree())
    }

}