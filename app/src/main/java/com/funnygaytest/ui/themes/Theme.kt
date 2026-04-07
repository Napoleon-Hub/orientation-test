package com.funnygaytest.ui.themes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.funnygaytest.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTheme(content: @Composable () -> Unit) {
    val colors = MainTestColors()

    val typography = MainTestTypography(
        heading = TextStyle(
            fontSize = 18.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_bold)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        buttonText = TextStyle(
            fontSize = 26.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_regular)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        description = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_medium)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        subText = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_regular)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        dialogTitle = TextStyle(
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_medium)),
            color = colors.secondaryText,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        dialogDescription = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.jet_brains_mono_regular)),
            color = colors.secondaryText,
            textAlign = TextAlign.Center
        )
    )

    CompositionLocalProvider(
        LocalGayTestColors provides colors,
        LocalGayTestTypography provides typography,
        content = content
    )
}