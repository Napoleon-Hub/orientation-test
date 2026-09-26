package com.funnygaytest.data.game

import com.funnygaytest.model.Question

data class GameSession(
    val isStarted: Boolean = false,
    val questions: List<Question> = emptyList(),
    val questionIndex: Int = 0,
    val health: Int = MAX_HEALTH
) {
    val isValid: Boolean get() = questionIndex in questions.indices

    val questionNumber: Int get() = questionIndex + 1

    companion object {
        const val MAX_HEALTH = 100

        fun newRun() = GameSession(isStarted = true, questions = generateNewGameRun())

        fun instantLoss() = GameSession(isStarted = true, health = 0)
    }
}
