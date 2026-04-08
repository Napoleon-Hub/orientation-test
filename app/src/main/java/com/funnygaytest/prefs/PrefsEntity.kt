package com.funnygaytest.prefs

import android.content.Context
import com.funnygaytest.prefs.types.activeBoolean
import com.funnygaytest.prefs.types.activeInt
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefsEntity @Inject constructor(@param:ApplicationContext val ctx: Context) {
    var isConnected by activeBoolean(false)
    var gameBegun by activeBoolean(false)
    var isMuted by activeBoolean(false)
    var lastQuestionIndex by activeInt(0)
    var points by activeInt(0)
}