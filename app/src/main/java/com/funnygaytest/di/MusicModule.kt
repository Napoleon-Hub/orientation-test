package com.funnygaytest.di

import com.funnygaytest.managers.music.AudioManager
import com.funnygaytest.managers.music.MusicController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicModule {

    @Binds
    abstract fun bindMusicController(audioManager: AudioManager): MusicController

}
