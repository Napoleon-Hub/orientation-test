package com.funnygaytest.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.funnygaytest.models.Answer
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun AnswersGroup(
    modifier: Modifier = Modifier,
    answers: List<Answer>,
    selectedAnswer: Answer?,
    onAnswerSelected: (Answer) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        answers.forEach { answer ->
            val isSelected = answer == selectedAnswer

            val borderColor by animateColorAsState(
                targetValue = if (isSelected) MainTestTheme.colors.primaryBackground else Color.Transparent,
                animationSpec = tween(durationMillis = 200),
                label = "BorderColorAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MainTestTheme.colors.primaryBackground.copy(alpha = 0.25f)
                        else Color.Black.copy(alpha = 0.4f)
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) borderColor else MainTestTheme.colors.primaryBackground.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onAnswerSelected(answer) }
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(id = answer.answerResId),
                    style = MainTestTheme.typography.subText,
                    color = if (isSelected) Color.White else MainTestTheme.colors.primaryText.copy(alpha = 0.8f),
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}