package com.funnygaytest.ui.themes.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.funnygaytest.R
import com.funnygaytest.domain.models.Answer
import com.funnygaytest.domain.models.Question
import com.funnygaytest.ui.themes.GayTestTheme

@Composable
fun RadioGroup(
    modifier: Modifier,
    question: Question,
    countOfQuestions: Int,
    previousSelectedAnswer: Answer,
    onAnswerClickRegister: (Answer) -> Unit
) {

    val (selectedAnswer, onAnswerSelected) = remember {
        mutableStateOf(previousSelectedAnswer)
    }

    Card(
        modifier,
        backgroundColor = GayTestTheme.colors.primaryElement,
        elevation = 6.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(Modifier.padding(8.dp)) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    text = stringResource(
                        id = R.string.game_questions_counter,
                        question.questionNumber,
                        countOfQuestions
                    ),
                    style = GayTestTheme.typography.subText
                )

                Text(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    text = stringResource(id = question.questionResId),
                    style = GayTestTheme.typography.subText
                )

            }

            question.listOfAnswers.forEach { answer ->
                Row(verticalAlignment = Alignment.CenterVertically) {

                    CompositionLocalProvider(
                        LocalRippleTheme provides ClearRippleTheme
                    ) {
                        RadioButton(
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.White,
                                unselectedColor = Color.White
                            ),
                            selected = (answer == selectedAnswer),
                            onClick = {
                                onAnswerSelected(answer)
                                onAnswerClickRegister(answer)
                            }
                        )
                    }

                    val annotatedString = buildAnnotatedString {
                        withStyle(style = SpanStyle()) { append("\n${stringResource(id = answer.answerResId)}\n") }
                    }

                    ClickableText(
                        text = annotatedString,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp),
                        style = GayTestTheme.typography.subText.copy(textAlign = TextAlign.Start),
                        onClick = {
                            onAnswerSelected(answer)
                            onAnswerClickRegister(answer)
                        }
                    )
                }
            }
        }
    }
}

object ClearRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor(): Color = Color.Transparent

    @Composable
    override fun rippleAlpha() = RippleAlpha(
        draggedAlpha = 0.0f,
        focusedAlpha = 0.0f,
        hoveredAlpha = 0.0f,
        pressedAlpha = 0.0f,
    )
}