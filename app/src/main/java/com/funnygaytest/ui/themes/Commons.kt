package com.funnygaytest.ui.themes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

data class GayTestColors(
    val primaryBackground: Color = Color(0xFF00BFFF),
    val primaryText: Color = Color(0xFFFFFFFF),
    val primaryElement: Color = Color(0xFF4169E1),
    val gray: Color = Color(0xFFD3D3D3)
)

data class GayTestTypography(
    val heading: TextStyle,
    val description: TextStyle,
    val subText: TextStyle,
    val buttonText: TextStyle,
    val dialogTitle: TextStyle,
    val dialogDescription: TextStyle,
)

data class GayTestShape(
    val padding: Dp,
    val cornersStyle: Shape
)

object GayTestTheme {
    val colors: GayTestColors
        @Composable
        get() = LocalGayTestColors.current

    val typography: GayTestTypography
        @Composable
        get() = LocalGayTestTypography.current

    val shapes: GayTestShape
        @Composable
        get() = LocalGayTestShape.current

}

enum class GayTestSize {
    Minimum, Small, Medium, Big
}

enum class GayTestCorners {
    Flat, Rounded
}

val LocalGayTestColors = staticCompositionLocalOf<GayTestColors> {
    error("No colors provided")
}

val LocalGayTestTypography = staticCompositionLocalOf<GayTestTypography> {
    error("No font provided")
}

val LocalGayTestShape = staticCompositionLocalOf<GayTestShape> {
    error("No shapes provided")
}