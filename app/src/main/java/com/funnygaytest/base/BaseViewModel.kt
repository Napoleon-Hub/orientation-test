package com.funnygaytest.base

import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import com.funnygaytest.prefs.PrefsEntity
import com.funnygaytest.managers.music.AudioManager
import kotlinx.coroutines.flow.update

abstract class BaseViewModel(
    private val preferences: PrefsEntity,
    private val audioManager: AudioManager
) : ViewModel() {

    protected val isConnected: Boolean
        get() = preferences.isConnected

    protected var gameBegun: Boolean
        get() = preferences.gameBegun
        set(value) {
            preferences.gameBegun = value
        }

    protected var isMuted: Boolean
        get() = preferences.isMuted
        set(value) {
            preferences.isMuted = value
        }

    protected var lastQuestionIndex: Int
        get() = preferences.lastQuestionIndex
        set(value) {
            preferences.lastQuestionIndex = value
        }

    var points: Int
        get() = preferences.points
        protected set(value) {
            preferences.points = value
        }

    fun playMusic(@RawRes resId: Int) {
        audioManager.playMusic(resId)
    }

    fun pauseMusic() {
        audioManager.stopMusic()
    }

    fun releaseMusic() {
        audioManager.release()
    }

    fun setMuteMusic(isMuted: Boolean) {
        audioManager.setMute(isMuted)
    }

    open fun toggleMusic() {
        isMuted = !isMuted
    }

}