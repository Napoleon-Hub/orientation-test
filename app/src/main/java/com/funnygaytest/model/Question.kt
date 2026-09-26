package com.funnygaytest.model

import androidx.annotation.Keep

@Keep
data class Question(
    val id: String, // Уникальный идентификатор (например "q_1", "q_10_a")
    val questionResId: Int,
    val listOfAnswers: List<Answer>
)