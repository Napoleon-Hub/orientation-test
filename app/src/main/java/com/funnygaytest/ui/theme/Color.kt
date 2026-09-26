package com.funnygaytest.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Палитра — сырые значения, в UI напрямую не используются, только через LabColors
private val LabGreen = Color(0xFF00FFC2)
private val LabBlack = Color(0xFF000000)
private val LabGraphite = Color(0xFF1A1A1A)
private val LabDarkGray = Color(0xFF444444)
private val LabRed = Color(0xFFFF3D00)
private val LabYellow = Color(0xFFFFFF00)
private val LabNeonGreen = Color(0xFF00FF00)
private val LabWhite = Color(0xFFFFFFFF)
private val LabLightGray = Color(0xFFE0E0E0)
private val LabGray = Color(0xFF9E9E9E)

/**
 * Семантические цвета приложения: имя описывает роль цвета, а не его значение.
 */
@Immutable
data class LabColors(
    val accent: Color = LabGreen,
    val background: Color = LabBlack,
    val surface: Color = LabGraphite,
    val panel: Color = LabBlack.copy(alpha = 0.3f),
    val panelStrong: Color = LabBlack.copy(alpha = 0.4f),
    val panelOpaque: Color = LabBlack.copy(alpha = 0.65f),
    val disabled: Color = LabDarkGray,

    val textPrimary: Color = LabLightGray,
    val textSecondary: Color = LabGray,
    val textEmphasis: Color = LabWhite,

    val error: Color = LabRed,
    val warning: Color = LabYellow,
    val success: Color = LabNeonGreen,

    // Шкала здоровья — от полного к критическому
    val healthFull: Color = Color(0xFF4CAF50),
    val healthHigh: Color = Color(0xFF8BC34A),
    val healthMedium: Color = Color(0xFFFFEB3B),
    val healthLow: Color = Color(0xFFFF9800),
    val healthCritical: Color = Color(0xFFF44336)
)
