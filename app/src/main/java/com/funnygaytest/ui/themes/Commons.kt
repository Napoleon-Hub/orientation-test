package com.funnygaytest.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class MainTestColors(
    val primaryBackground: Color = Color(0xFF00BFFF),
    val primaryText: Color = Color(0xFFFFFFFF),
    val primaryElement: Color = Color(0xFF4169E1),
    val gray: Color = Color(0xFFD3D3D3)
)

data class MainTestTypography(
    val heading: TextStyle,
    val description: TextStyle,
    val subText: TextStyle,
    val buttonText: TextStyle,
    val dialogTitle: TextStyle,
    val dialogDescription: TextStyle,
)

data class MainTestShape(
    val padding: Dp,
    val cornersStyle: Shape
)

object MainTestTheme {
    val colors: MainTestColors
        @Composable
        get() = LocalGayTestColors.current

    val typography: MainTestTypography
        @Composable
        get() = LocalGayTestTypography.current

    val shapes: MainTestShape
        @Composable
        get() = LocalGayTestShape.current

}

enum class MainTestSize {
    Minimum, Small, Medium, Big
}

enum class MainTestCorners {
    Flat, Rounded
}

val LocalGayTestColors = staticCompositionLocalOf<MainTestColors> {
    error("No colors provided")
}

val LocalGayTestTypography = staticCompositionLocalOf<MainTestTypography> {
    error("No font provided")
}

val LocalGayTestShape = staticCompositionLocalOf<MainTestShape> {
    error("No shapes provided")
}