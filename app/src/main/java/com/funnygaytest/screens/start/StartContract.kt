package com.funnygaytest.screens.start

import com.funnygaytest.base.UiEffect
import com.funnygaytest.base.UiEvent
import com.funnygaytest.base.UiState

class StartContract {

    sealed class Event : UiEvent {
        object OnNextClick : Event()
    }

    sealed class State : UiState {
        object ViewStateGameNotStarted: State()
        object ViewStateGameStarted: State()
    }

    sealed class Effect : UiEffect {
        object GoToGameScreen : Effect()
        object ShowConnectionErrorDialog : Effect()
    }
}