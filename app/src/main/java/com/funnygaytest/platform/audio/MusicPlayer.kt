package com.funnygaytest.platform.audio

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import androidx.annotation.RawRes
import com.funnygaytest.data.settings.SettingsRepository
import com.funnygaytest.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    @param:ApplicationScope private val scope: CoroutineScope
) : MusicController {

    private var mediaPlayer: MediaPlayer? = null
    private var currentResId: Int? = null

    private val storedMuted: StateFlow<Boolean?> = settingsRepository.isMuted
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val isMuted: StateFlow<Boolean> = storedMuted
        .map { it == true }
        .stateIn(scope, SharingStarted.Eagerly, false)

    private val volume: Float
        get() = if (storedMuted.value == false) MUSIC_VOLUME else 0f

    init {
        scope.launch {
            storedMuted.collect { mediaPlayer?.setVolume(volume, volume) }
        }
    }

    override fun playMusic(@RawRes resId: Int) {
        if (currentResId == resId && mediaPlayer?.isPlaying == true) return

        stopMusic()

        currentResId = resId

        val attributedContext = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.createAttributionContext("audio_tag")
        } else context

        mediaPlayer = MediaPlayer.create(attributedContext, resId).apply {
            isLooping = true
            setVolume(volume, volume)
            start()
        }
    }

    override fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        currentResId = null
    }

    override fun toggleMute() {
        scope.launch { settingsRepository.toggleMuted() }
    }

    private companion object {
        const val MUSIC_VOLUME = 0.5f
    }
}
