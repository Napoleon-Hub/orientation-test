package com.funnygaytest.data.game

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.funnygaytest.models.Question
import com.funnygaytest.utils.helpers.findQuestionById
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameSessionRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    val session: Flow<GameSession> = dataStore.data
        .map { it.toGameSession() }
        .distinctUntilChanged()

    suspend fun update(transform: (GameSession) -> GameSession) {
        dataStore.edit { prefs -> prefs.write(transform(prefs.toGameSession())) }
    }

    suspend fun clear() {
        update { GameSession() }
    }

    private fun Preferences.toGameSession() = GameSession(
        isStarted = this[IS_STARTED] ?: false,
        questions = decodeQuestions(this[QUESTION_IDS]),
        questionIndex = this[QUESTION_INDEX] ?: 0,
        health = this[HEALTH] ?: GameSession.MAX_HEALTH
    )

    private fun MutablePreferences.write(session: GameSession) {
        this[IS_STARTED] = session.isStarted
        this[QUESTION_IDS] = Json.encodeToString(session.questions.map { it.id })
        this[QUESTION_INDEX] = session.questionIndex
        this[HEALTH] = session.health
    }

    private fun decodeQuestions(json: String?): List<Question> {
        if (json == null) return emptyList()
        val ids = try {
            Json.parseToJsonElement(json).jsonArray.map { element ->
                val idElement = (element as? JsonObject)?.get("id") ?: element
                idElement.jsonPrimitive.content
            }
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Saved question list is not a valid JSON array")
            return emptyList()
        }
        val questions = ids.mapNotNull(::findQuestionById)
        if (questions.size != ids.size) {
            Timber.w("Saved run contains unknown questions, the run is discarded")
            return emptyList()
        }
        return questions
    }

    private companion object {
        val IS_STARTED = booleanPreferencesKey("gameBegun")
        val QUESTION_IDS = stringPreferencesKey("currentQuestionList")
        val QUESTION_INDEX = intPreferencesKey("lastQuestionIndex")
        val HEALTH = intPreferencesKey("health")
    }
}
