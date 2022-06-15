package com.funnygaytest.domain.model

import androidx.annotation.Keep

@Keep
data class Question(
    val questionNumber: Int,
    val questionResId: Int,
    val listOfAnswers: List<Answer>
)