package com.funnygaytest.ui.screens.connection

import androidx.lifecycle.ViewModel
import com.funnygaytest.platform.audio.MusicController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoInternetViewModel @Inject constructor(
    musicController: MusicController
) : ViewModel(), MusicController by musicController
