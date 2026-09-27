package com.funnygaytest.ui.utils

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.funnygaytest.platform.audio.MusicController

@Composable
fun ScreenMusic(@RawRes resId: Int, musicController: MusicController) {
    LifecycleResumeEffect(resId, musicController) {
        musicController.playMusic(resId)
        onPauseOrDispose { musicController.stopMusic(resId) }
    }
}
