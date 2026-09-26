package com.funnygaytest.model

data class Question(
    val id: String, // Уникальный идентификатор (например "q_1", "q_10_a")
    val questionResId: Int,
    val listOfAnswers: List<Answer>
)