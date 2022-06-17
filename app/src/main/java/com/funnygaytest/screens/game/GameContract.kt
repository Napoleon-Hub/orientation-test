package com.funnygaytest.screens.game

import com.funnygaytest.base.UiEffect
import com.funnygaytest.base.UiEvent
import com.funnygaytest.base.UiState
import com.funnygaytest.domain.model.Answer

class GameContract {

    sealed class Event : UiEvent {
        data class OnAnswerClick(val answer: Answer) : Event()
        data class OnNextClick(val isFinish: Boolean) : Event()
    }

    sealed class State : UiState {
        object ViewStatePlayGame: State()
        object ViewStateFinishGame: State()
    }

    sealed class Effect : UiEffect {
        object NavigateToResultScreen : Effect()
        object ShowConnectionErrorDialog : Effect()
        object ShowChooseAnswerToast : Effect()
    }

}