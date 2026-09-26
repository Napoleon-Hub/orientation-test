package com.funnygaytest.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.funnygaytest.models.Answer
import com.funnygaytest.ui.theme.LabTheme

private val FadeHeight = 32.dp
private val ScrollIndicatorGap = 12.dp
private val AnswerShape = RoundedCornerShape(12.dp)

@Composable
fun AnswersGroup(
    modifier: Modifier = Modifier,
    answers: List<Answer>,
    selectedAnswer: Answer?,
    textStyle: TextStyle = LabTheme.typography.body,
    onAnswerSelected: (Answer) -> Unit
) {
    val scrollState = rememberScrollState()

    LaunchedEffect(answers) {
        scrollState.scrollTo(0)
    }

    Column(
        modifier = modifier
            .verticalScrollIndicator(scrollState, color = LabTheme.colors.accent.copy(alpha = 0.6f))
            .verticalFadingEdges(scrollState, FadeHeight)
            .verticalScroll(scrollState)
            .padding(end = ScrollIndicatorGap),
        verticalArrangement = Arrangement.spacedBy(LabTheme.dimens.spacingMedium)
    ) {
        answers.forEach { answer ->
            AnswerItem(
                answer = answer,
                isSelected = answer == selectedAnswer,
                textStyle = textStyle,
                onClick = { onAnswerSelected(answer) }
            )
        }
    }
}

@Composable
private fun AnswerItem(
    answer: Answer,
    isSelected: Boolean,
    textStyle: TextStyle,
    onClick: () -> Unit
) {
    val colors = LabTheme.colors
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.accent else colors.accent.copy(alpha = 0.1f),
        animationSpec = tween(durationMillis = 200),
        label = "AnswerBorderColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(AnswerShape)
            .background(if (isSelected) colors.accent.copy(alpha = 0.25f) else colors.panelStrong)
            .border(width = if (isSelected) 2.dp else 1.dp, color = borderColor, shape = AnswerShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = stringResource(answer.answerResId),
            style = textStyle,
            color = if (isSelected) colors.textEmphasis else colors.textPrimary.copy(alpha = 0.8f),
            textAlign = TextAlign.Start
        )
    }
}
