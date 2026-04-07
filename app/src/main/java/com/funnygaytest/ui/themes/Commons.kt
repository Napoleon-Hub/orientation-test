package com.funnygaytest.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

// UI Цвета
val LabGreen = Color(0xFF00FFC2)
val LabDarkBackground = Color(0xFF000000)
val LabSurface = Color(0xFF1A1A1A)
val LabError = Color(0xFFFF3D00)

// Текстовые цвета
val LabTextPrimary = Color(0xFFE0E0E0)
val LabTextSecondary = Color(0xFF9E9E9E)


data class MainTestColors(
    val primaryBackground: Color = LabGreen,
    val primaryText: Color = LabTextPrimary,
    val primaryElement: Color = LabSurface,
    val secondaryText: Color = LabTextSecondary,
    val error: Color = LabError
)

data class MainTestTypography(
    val heading: TextStyle,
    val description: TextStyle,
    val subText: TextStyle,
    val buttonText: TextStyle,
    val dialogTitle: TextStyle,
    val dialogDescription: TextStyle
)

object MainTestTheme {
    val colors: MainTestColors
        @Composable
        get() = LocalGayTestColors.current

    val typography: MainTestTypography
        @Composable
        get() = LocalGayTestTypography.current

}

val LocalGayTestColors = staticCompositionLocalOf<MainTestColors> {
    error("No colors provided")
}

val LocalGayTestTypography = staticCompositionLocalOf<MainTestTypography> {
    error("No font provided")
}