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
    paddingSize: GayTestSize = GayTestSize.Medium,
    corners: GayTestCorners = GayTestCorners.Rounded,
    content: @Composable () -> Unit
) {
    val colors = GayTestColors()

    val typography = GayTestTypography(
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

    val shapes = GayTestShape(
        padding = when (paddingSize) {
            GayTestSize.Minimum -> 10.dp
            GayTestSize.Small -> 12.dp
            GayTestSize.Medium -> 16.dp
            GayTestSize.Big -> 20.dp
        },
        cornersStyle = when (corners) {
            GayTestCorners.Flat -> RoundedCornerShape(0.dp)
            GayTestCorners.Rounded -> RoundedCornerShape(8.dp)
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