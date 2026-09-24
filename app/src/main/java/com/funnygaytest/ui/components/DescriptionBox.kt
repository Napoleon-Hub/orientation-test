package com.funnygaytest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun DescriptionBox(
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    textAlign: TextAlign = TextAlign.Center,
    descriptionString: AnnotatedString
) {
    Box(
        modifier = modifier
            .background(
                LabTheme.colors.panel,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = LabTheme.colors.accent.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = descriptionString,
            style = textStyle.copy(color = LabTheme.colors.textPrimary, textAlign = textAlign),
            autoSize = shrinkToFit(textStyle)
        )
    }
}