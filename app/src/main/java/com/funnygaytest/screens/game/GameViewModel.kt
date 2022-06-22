package com.funnygaytest.screens.game

import android.content.Context
import android.content.res.Configuration
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.funnygaytest.base.BaseViewModel
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.models.Answer
import com.funnygaytest.domain.models.Question
import com.funnygaytest.utils.helpers.QuestionsGenerator
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

private const val EVENT_QUESTION = "question_"
private const val LOCALE_RU = "ru"
private const val LOCALE_EN = "en"

@HiltViewModel
class GameViewModel @Inject constructor(
    preferences: PrefsEntity,
    private val firebaseAnalytics: FirebaseAnalytics
) : BaseViewModel<GameContract.Event, GameContract.State, GameContract.Effect>(preferences) {

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
                        sendAnswerQuestionEvent(lastQuestionIndex + 1, getLocalizedString(event.context))
                        points += selectedAnswer.answerPoints
                        if (!event.isFinish) {
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

    private fun getLocalizedString(context: Context): String {
        return if (context.resources.configuration.locales[0].language == LOCALE_RU) {
            val conf = Configuration(context.resources.configuration)
            conf.setLocale(Locale(LOCALE_EN))
            context.createConfigurationContext(conf).getString(selectedAnswer.answerResId)
        } else context.getString(selectedAnswer.answerResId)
    }

    private fun sendAnswerQuestionEvent(questionNumber: Int, answer: String) {
        firebaseAnalytics.logEvent(EVENT_QUESTION + questionNumber) {
            param(FirebaseAnalytics.Param.ITEM_NAME, answer)
        }
    }

}