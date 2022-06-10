package com.funnygaytest.screens.game.models

sealed class GameEvent {
    object ChooseAnswer : GameEvent()
    object NextClicked : GameEvent()
    object FinishClicked : GameEvent()
}