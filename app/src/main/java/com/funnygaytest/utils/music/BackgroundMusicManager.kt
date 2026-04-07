package com.funnygaytest.utils.music

import android.content.Context
import android.media.MediaPlayer
import com.funnygaytest.R

class BackgroundMusicManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun startMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.start_music).apply {
                isLooping = true // Зацикливание
                start()
            }
        } else if (!mediaPlayer!!.isPlaying) {
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
    }

    fun toggleMute(isMuted: Boolean) {
        if (isMuted) {
            mediaPlayer?.setVolume(0f, 0f)
        } else {
            mediaPlayer?.setVolume(1f, 1f)
        }
    }

    fun release() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}