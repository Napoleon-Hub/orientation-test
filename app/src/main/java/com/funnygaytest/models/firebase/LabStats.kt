package com.funnygaytest.models.firebase

data class LabStats(
    val wins: Int = 0,
    val losses: Int = 0,
    val achievements: List<String> = emptyList()
)