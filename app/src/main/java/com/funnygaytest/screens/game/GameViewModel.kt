package com.funnygaytest.screens.game

import androidx.lifecycle.ViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(preferences: PrefsEntity) : ViewModel() {


}