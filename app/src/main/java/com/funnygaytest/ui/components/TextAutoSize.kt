package com.funnygaytest.ui.components

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.modifiers.TextAutoSizeLayoutScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private val DefaultMinFontSize = 8.sp
private val DefaultMaxFontSize = 14.sp
private val StepSize = 0.5.sp

fun shrinkToFit(style: TextStyle, minFontSize: TextUnit = DefaultMinFontSize): TextAutoSize =
    TextAutoSize.StepBased(
        minFontSize = minFontSize,
        maxFontSize = style.maxFontSize(),
        stepSize = StepSize
    )

fun shrinkToFitAll(
    style: TextStyle,
    texts: List<String>,
    minFontSize: TextUnit = DefaultMinFontSize
): TextAutoSize = GroupShrinkToFit(
    texts = texts.map(::AnnotatedString),
    minSteps = (minFontSize.value / StepSize.value).toInt(),
    maxSteps = (style.maxFontSize().value / StepSize.value).toInt()
)

private fun TextStyle.maxFontSize(): TextUnit = if (fontSize.isSp) fontSize else DefaultMaxFontSize

private data class GroupShrinkToFit(
    val texts: List<AnnotatedString>,
    val minSteps: Int,
    val maxSteps: Int
) : TextAutoSize {

    override fun TextAutoSizeLayoutScope.getFontSize(constraints: Constraints, text: AnnotatedString): TextUnit {
        val group = texts + text
        var low = minSteps
        var high = maxSteps
        while (low < high) {
            val mid = (low + high + 1) / 2
            val fontSize = (mid * StepSize.value).sp
            if (group.all { !performLayout(constraints, it, fontSize).hasVisualOverflow }) low = mid else high = mid - 1
        }
        return (low * StepSize.value).sp
    }
}
