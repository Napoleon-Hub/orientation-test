package com.funnygaytest.ui.components

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.funnygaytest.managers.music.MusicController

@Composable
fun ScreenMusic(@RawRes resId: Int, musicController: MusicController) {
    LifecycleResumeEffect(resId, musicController) {
        musicController.playMusic(resId)
        onPauseOrDispose { musicController.stopMusic() }
    }
}
