package com.funnygaytest.ui.components

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

fun shrinkToFit(style: TextStyle, minFontSize: TextUnit = 8.sp): TextAutoSize {
    val maxFontSize = if (style.fontSize.isSp) style.fontSize else 14.sp
    return TextAutoSize.StepBased(
        minFontSize = minFontSize,
        maxFontSize = maxFontSize,
        stepSize = 0.5.sp
    )
}
