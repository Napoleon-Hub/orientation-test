package com.funnygaytest

import androidx.lifecycle.ViewModel
import com.funnygaytest.managers.firestore.FirestoreManager
import com.funnygaytest.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


data class MainUiState(val consentShown: Boolean)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val firestoreManager: FirestoreManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(consentShown = preferences.consentShown)
    )
    val uiState = _uiState.asStateFlow()

    fun updateConsentState() {
        preferences.consentShown = true
        _uiState.update { it.copy(consentShown = true) }
    }

    fun authorizeFirebase() {
        firestoreManager.authorizeFirebase()
    }

}