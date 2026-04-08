package com.funnygaytest.models

import androidx.annotation.Keep

@Keep
data class Answer(
    val answerResId: Int,
    val hpChange: Int // Example: -30 +10 0
)