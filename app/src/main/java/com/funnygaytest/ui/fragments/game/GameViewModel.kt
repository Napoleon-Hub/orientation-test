package com.funnygaytest.ui.fragments.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.data.room.entity.QuestionsEntity
import com.funnygaytest.domain.local.questions.QuestionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val questionsRepository: QuestionsRepository
) : ViewModel() {

    private var _currentQuestion = MutableLiveData<QuestionsEntity>()
    val currentQuestion: LiveData<QuestionsEntity> = _currentQuestion

    private var _totalQuestionsNumber = MutableLiveData<Int>()
    val totalQuestionsNumber: LiveData<Int> = _totalQuestionsNumber

    fun loadQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentQuestion.postValue(questionsRepository.getQuestionById(lastQuestion))
        }
    }

    fun getTotalQuestionsNumber() {
        viewModelScope.launch(Dispatchers.IO) {
            _totalQuestionsNumber.postValue(questionsRepository.getEntitiesCount())
        }
    }

    var lastQuestion: Int
        get() = preferences.lastQuestionIndex
        set(value) { preferences.lastQuestionIndex = value }

    var points: Int
        get() = preferences.points
        set(value) { preferences.points = value }

    val isConnected: Boolean
        get() = preferences.isConnected

}