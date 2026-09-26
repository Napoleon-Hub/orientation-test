package com.funnygaytest.di

import com.funnygaytest.platform.audio.MusicController
import com.funnygaytest.platform.audio.MusicPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MusicModule {

    @Binds
    abstract fun bindMusicController(musicPlayer: MusicPlayer): MusicController

}
