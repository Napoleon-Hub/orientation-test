package com.funnygaytest.prefs

import android.content.Context
import com.funnygaytest.prefs.types.activeBoolean
import com.funnygaytest.prefs.types.activeInt
import com.funnygaytest.prefs.types.activeQuestionList
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PrefsEntity @Inject constructor(@param:ApplicationContext val ctx: Context) {
    var gameBegun by activeBoolean(false)
    var isMuted by activeBoolean(false)
    var lastQuestionIndex by activeInt(0)
    var health by activeInt(100)
    var currentQuestionList by activeQuestionList()
    var pussyModeChosen by activeBoolean(false)
    var countOfLoses by activeInt(0)
    var countOfWins by activeInt(0)

    // Endings
    var endingWin1 by activeInt(0)
    var endingWin33 by activeInt(0)
    var endingWin66 by activeInt(0)
    var endingWin100 by activeInt(0)
    var endingLose4 by activeInt(0)
    var endingLose8 by activeInt(0)
    var endingLose12 by activeInt(0)
    var endingLose16 by activeInt(0)
    var endingLose20 by activeInt(0)
    var endingLosePussy by activeInt(0)
    var endingAll by activeInt(0)
    var endingDonate by activeInt(0)

}