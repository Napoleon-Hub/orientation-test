package com.funnygaytest.platform.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun isLoggable(tag: String?, priority: Int): Boolean = priority >= Log.WARN

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority < Log.INFO) return
        val logTag = tag ?: "FunnyGayTest"
        Log.println(priority, logTag, message)
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.log("$logTag: $message")
        if (t != null) crashlytics.recordException(t)
    }
}
