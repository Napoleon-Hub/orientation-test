package com.funnygaytest.ui.themes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnygaytest.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainTheme(
    paddingSize: MainTestSize = MainTestSize.Medium,
    corners: MainTestCorners = MainTestCorners.Rounded,
    content: @Composable () -> Unit
) {
    val colors = MainTestColors()

    val typography = MainTestTypography(
        heading = TextStyle(
            fontSize = 40.sp,
            fontFamily = FontFamily(Font(R.font.appetite_italic)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        buttonText = TextStyle(
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.appetite_italic)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        description = TextStyle(
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.appetite)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        subText = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.appetite)),
            color = colors.primaryText,
            textAlign = TextAlign.Center
        ),
        dialogTitle = TextStyle(
            fontSize = 22.sp,
            fontFamily = FontFamily(Font(R.font.appetite)),
            color = colors.gray,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        ),
        dialogDescription = TextStyle(
            fontSize = 15.sp,
            fontFamily = FontFamily(Font(R.font.appetite)),
            color = colors.gray,
            textAlign = TextAlign.Center
        )
    )

    val shapes = MainTestShape(
        padding = when (paddingSize) {
            MainTestSize.Minimum -> 10.dp
            MainTestSize.Small -> 12.dp
            MainTestSize.Medium -> 16.dp
            MainTestSize.Big -> 20.dp
        },
        cornersStyle = when (corners) {
            MainTestCorners.Flat -> RoundedCornerShape(0.dp)
            MainTestCorners.Rounded -> RoundedCornerShape(8.dp)
        }
    )

    CompositionLocalProvider(
        LocalOverScrollConfiguration provides null,
        LocalGayTestColors provides colors,
        LocalGayTestTypography provides typography,
        LocalGayTestShape provides shapes,
        content = content
    )
}