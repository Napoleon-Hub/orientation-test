package com.funnygaytest.screens.result

import com.funnygaytest.base.UiEffect
import com.funnygaytest.base.UiEvent
import com.funnygaytest.base.UiState


class ResultContract {

    sealed class Event : UiEvent {
        object OnShareResultsClick : Event()
        object OnRestartGameClick : Event()
        object OnRateGameClick : Event()
        object OnPayClick : Event()
    }

    sealed class State : UiState {
        object ViewStateGameResult: State()
    }

    sealed class Effect : UiEffect {
        object ShowConnectionErrorDialog : Effect()
    }

}