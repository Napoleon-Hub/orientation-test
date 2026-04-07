package com.funnygaytest.utils.music

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null
    private var isMuted: Boolean = false

    fun playMusic(@RawRes resId: Int, isLooping: Boolean = true) {
        if (currentResId == resId && mediaPlayer?.isPlaying == true) return

        stopMusic()

        currentResId = resId
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            this.isLooping = isLooping
            setVolume(if (isMuted) 0f else 0.6f, if (isMuted) 0f else 0.6f)
            start()
        }
    }

    fun setMute(mute: Boolean) {
        isMuted = mute
        val volume = if (isMuted) 0f else 0.6f
        mediaPlayer?.setVolume(volume, volume)
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = null
    }

    fun release() {
        stopMusic()
    }
}