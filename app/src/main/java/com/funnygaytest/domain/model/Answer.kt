package com.funnygaytest.domain.model

import androidx.annotation.Keep

@Keep
data class Answer(
    val answerResId: Int = -1,
    val answerPoints: Int = -1
)