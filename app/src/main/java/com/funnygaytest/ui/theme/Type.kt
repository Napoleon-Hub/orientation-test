package com.funnygaytest.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.funnygaytest.R

val JetBrainsMono = FontFamily(
    Font(R.font.jet_brains_mono_regular, FontWeight.Normal),
    Font(R.font.jet_brains_mono_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.jet_brains_mono_medium, FontWeight.Medium),
    Font(R.font.jet_brains_mono_bold, FontWeight.Bold)
)

@Immutable
data class LabTypography(
    val heading: TextStyle,
    val description: TextStyle,
    val body: TextStyle,
    val button: TextStyle,
    val caption: TextStyle
)

internal fun labTypography(colors: LabColors): LabTypography {
    val base = TextStyle(
        fontFamily = JetBrainsMono,
        color = colors.textPrimary,
        textAlign = TextAlign.Center
    )
    return LabTypography(
        heading = base.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        description = base.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
        body = base.copy(fontSize = 15.sp),
        button = base.copy(fontSize = 26.sp),
        caption = base.copy(fontSize = 12.sp, fontStyle = FontStyle.Italic)
    )
}

internal val MaterialTypography: Typography = Typography().run {
    copy(
        displayLarge = displayLarge.copy(fontFamily = JetBrainsMono),
        displayMedium = displayMedium.copy(fontFamily = JetBrainsMono),
        displaySmall = displaySmall.copy(fontFamily = JetBrainsMono),
        headlineLarge = headlineLarge.copy(fontFamily = JetBrainsMono),
        headlineMedium = headlineMedium.copy(fontFamily = JetBrainsMono),
        headlineSmall = headlineSmall.copy(fontFamily = JetBrainsMono),
        titleLarge = titleLarge.copy(fontFamily = JetBrainsMono),
        titleMedium = titleMedium.copy(fontFamily = JetBrainsMono),
        titleSmall = titleSmall.copy(fontFamily = JetBrainsMono),
        bodyLarge = bodyLarge.copy(fontFamily = JetBrainsMono),
        bodyMedium = bodyMedium.copy(fontFamily = JetBrainsMono),
        bodySmall = bodySmall.copy(fontFamily = JetBrainsMono),
        labelLarge = labelLarge.copy(fontFamily = JetBrainsMono),
        labelMedium = labelMedium.copy(fontFamily = JetBrainsMono),
        labelSmall = labelSmall.copy(fontFamily = JetBrainsMono)
    )
}
