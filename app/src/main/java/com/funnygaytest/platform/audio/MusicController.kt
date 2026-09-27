package com.funnygaytest.platform.audio

import androidx.annotation.RawRes
import kotlinx.coroutines.flow.StateFlow

interface MusicController {
    val isMuted: StateFlow<Boolean>
    fun playMusic(@RawRes resId: Int)
    fun stopMusic(@RawRes resId: Int)
    fun toggleMute()
}
