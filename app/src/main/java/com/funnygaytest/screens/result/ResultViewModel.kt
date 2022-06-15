package com.funnygaytest.screens.result

import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(preferences: PrefsEntity) :
    BaseViewModel<ResultContract.Event, ResultContract.State, ResultContract.Effect>(preferences)  {

    override fun createInitialState(): ResultContract.State = ResultContract.State.ViewStateGameResult

    override fun handleEvent(event: ResultContract.Event) {
        when(event) {
            ResultContract.Event.OnPayClick -> {

            }
            ResultContract.Event.OnRateGameClick -> {

            }
            ResultContract.Event.OnRestartGameClick -> {

            }
            ResultContract.Event.OnShareResultsClick -> {

            }
        }
    }


}