package com.funnygaytest.ui.theme

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

/**
 * Размеры, зависящие от размера окна. Приложение работает только в ландшафте,
 * поэтому ограничивающее измерение — высота: телефон ~320–430dp, планшет ~700–1000dp.
 */
@Immutable
data class LabDimens(
    val screenPadding: Dp,
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,
    val buttonHeight: Dp,
    // Множитель для размеров шрифтов LabTypography
    val textScale: Float
)

internal val CompactDimens = LabDimens(
    screenPadding = 24.dp,
    spacingSmall = 4.dp,
    spacingMedium = 12.dp,
    spacingLarge = 16.dp,
    buttonHeight = 48.dp,
    textScale = 1f
)

internal val MediumDimens = LabDimens(
    screenPadding = 32.dp,
    spacingSmall = 6.dp,
    spacingMedium = 16.dp,
    spacingLarge = 24.dp,
    buttonHeight = 64.dp,
    textScale = 1.25f
)

internal val ExpandedDimens = LabDimens(
    screenPadding = 48.dp,
    spacingSmall = 8.dp,
    spacingMedium = 20.dp,
    spacingLarge = 32.dp,
    buttonHeight = 80.dp,
    textScale = 1.5f
)

@Composable
internal fun currentLabDimens(): LabDimens {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isWideEnough = sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
    return when {
        !isWideEnough -> CompactDimens
        sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND) -> ExpandedDimens
        sizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) -> MediumDimens
        else -> CompactDimens
    }
}

fun TextStyle.scaled(factor: Float): TextStyle =
    if (factor == 1f) this else copy(fontSize = fontSize * factor)
