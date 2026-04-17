package com.funnygaytest.managers.music

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
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

        val attributedContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.createAttributionContext("audio_tag")
        } else context

        mediaPlayer = MediaPlayer.create(attributedContext, resId).apply {
            this.isLooping = isLooping
            setVolume(if (isMuted) 0f else 0.5f, if (isMuted) 0f else 0.5f)
            start()
        }
    }

    fun setMute(mute: Boolean) {
        isMuted = mute
        mediaPlayer?.let {
            val volume = if (isMuted) 0f else 0.5f
            it.setVolume(volume, volume)
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = null
    }
}