package com.funnygaytest.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val DefaultColors = LabColors()
private val DefaultTypography = labTypography(DefaultColors)

private val LabColorScheme = darkColorScheme(
    primary = DefaultColors.accent,
    onPrimary = DefaultColors.background,
    background = DefaultColors.background,
    onBackground = DefaultColors.textPrimary,
    surface = DefaultColors.surface,
    onSurface = DefaultColors.textPrimary,
    onSurfaceVariant = DefaultColors.textSecondary,
    error = DefaultColors.error,
    onError = DefaultColors.background
)

val LocalLabColors = staticCompositionLocalOf<LabColors> {
    error("LabColors не предоставлены: оберните UI в LabTheme")
}

val LocalLabTypography = staticCompositionLocalOf<LabTypography> {
    error("LabTypography не предоставлена: оберните UI в LabTheme")
}

@Composable
fun LabTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLabColors provides DefaultColors,
        LocalLabTypography provides DefaultTypography
    ) {
        MaterialTheme(
            colorScheme = LabColorScheme,
            typography = MaterialTypography,
            content = content
        )
    }
}

object LabTheme {
    val colors: LabColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLabColors.current

    val typography: LabTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalLabTypography.current
}
