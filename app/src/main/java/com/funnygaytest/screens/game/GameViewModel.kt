package com.funnygaytest.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.models.Answer
import com.funnygaytest.domain.models.Question
import com.funnygaytest.utils.helpers.QuestionsGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(preferences: PrefsEntity) :
    BaseViewModel<GameContract.Event, GameContract.State, GameContract.Effect>(preferences) {

    val listOfQuestions = QuestionsGenerator().generateQuestions()

    private var _lastQuestion: MutableLiveData<Question> =
        MutableLiveData(listOfQuestions[lastQuestionIndex])
    val lastQuestion: LiveData<Question> = _lastQuestion

    var selectedAnswer: Answer = Answer()
        private set

    override fun createInitialState(): GameContract.State =
        if (lastQuestionIndex == 23) GameContract.State.ViewStateFinishGame
        else GameContract.State.ViewStatePlayGame

    override fun handleEvent(event: GameContract.Event) {
        when (event) {
            is GameContract.Event.OnAnswerClick -> {
                selectedAnswer = event.answer
            }
            is GameContract.Event.OnNextClick -> {
                if (selectedAnswer.answerPoints != -1) {
                    if (isConnected) {
                        if (!event.isFinish) {
                            points += selectedAnswer.answerPoints
                            changeQuestion()
                        } else {
                            setEffect { GameContract.Effect.NavigateToResultScreen }
                        }
                    } else {
                        setEffect { GameContract.Effect.ShowConnectionErrorDialog }
                    }
                } else {
                    setEffect { GameContract.Effect.ShowChooseAnswerToast }
                }
            }
        }
    }

    private fun changeQuestion() {
        selectedAnswer = Answer()
        lastQuestionIndex++
        _lastQuestion.value = listOfQuestions[lastQuestionIndex]
        if (listOfQuestions.last() == _lastQuestion.value) {
            setState { GameContract.State.ViewStateFinishGame }
        }
    }

}