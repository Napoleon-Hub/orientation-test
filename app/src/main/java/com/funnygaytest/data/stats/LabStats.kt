package com.funnygaytest.data.stats

import androidx.annotation.Keep

@Keep
data class LabStats(
    val wins: Int = 0,
    val losses: Int = 0,
    val achievements: List<String> = emptyList()
)