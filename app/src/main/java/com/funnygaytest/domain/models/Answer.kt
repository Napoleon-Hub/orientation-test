package com.funnygaytest.domain.models

import androidx.annotation.Keep

@Keep
data class Answer(
    val answerResId: Int = -1,
    val answerPoints: Int = -1
)