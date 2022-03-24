package com.funnygaytest.ui.fragments.start

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.funnygaytest.data.prefs.PrefsEntity
import com.funnygaytest.domain.local.questions.QuestionsRepository
import com.funnygaytest.utils.helpers.QuestionsGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val preferences: PrefsEntity,
    private val questionsRepository: QuestionsRepository
) : ViewModel() {

    fun fillQuestionsDatabase(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val questions = QuestionsGenerator(context).generateQuestions()
            questionsRepository.insertQuestions(questions)
        }
    }

    var gameBegun: Boolean
        get() = preferences.gameBegun
        set(value) { preferences.gameBegun = value }

    val isConnected: Boolean
        get() = preferences.isConnected

    val lastQuestion: Int
        get() = preferences.lastQuestion

}