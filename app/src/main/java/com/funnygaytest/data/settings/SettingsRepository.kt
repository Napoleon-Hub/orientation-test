package com.funnygaytest.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val isMuted: Flow<Boolean> = dataStore.data
        .map { it[IS_MUTED] ?: false }
        .distinctUntilChanged()

    val consentShown: Flow<Boolean> = dataStore.data
        .map { it[CONSENT_SHOWN] ?: false }
        .distinctUntilChanged()

    suspend fun toggleMuted() {
        dataStore.edit { it[IS_MUTED] = !(it[IS_MUTED] ?: false) }
    }

    suspend fun setConsentShown() {
        dataStore.edit { it[CONSENT_SHOWN] = true }
    }

    private companion object {
        val IS_MUTED = booleanPreferencesKey("isMuted")
        val CONSENT_SHOWN = booleanPreferencesKey("consentShown")
    }
}
