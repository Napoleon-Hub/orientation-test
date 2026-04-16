package com.funnygaytest.prefs

import android.content.Context
import com.funnygaytest.prefs.types.activeBoolean
import com.funnygaytest.prefs.types.activeInt
import com.funnygaytest.prefs.types.activeQuestionList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefsEntity @Inject constructor(@param:ApplicationContext val ctx: Context) {
    var gameBegun by activeBoolean(false)
    var consentShown by activeBoolean(false)
    var isMuted by activeBoolean(false)
    var lastQuestionIndex by activeInt(0)
    var health by activeInt(100)
    var currentQuestionList by activeQuestionList()
}