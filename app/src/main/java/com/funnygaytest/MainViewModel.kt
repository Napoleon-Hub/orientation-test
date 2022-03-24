package com.funnygaytest

import androidx.lifecycle.ViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val preferences: PrefsEntity) : ViewModel() {

    fun setConnection(connectionStatus: Boolean) {
        isConnected = connectionStatus
    }

    private var isConnected: Boolean
        get() = preferences.isConnected
        set(value) { preferences.isConnected = value }

}