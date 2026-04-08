package com.funnygaytest.ui.screens.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.funnygaytest.R
import com.funnygaytest.models.Answer
import com.funnygaytest.ui.components.AnswersGroup
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme
import com.funnygaytest.utils.helpers.QuestionsGenerator

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    goToResult: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pauseMusic()
                }

                Lifecycle.Event.ON_RESUME -> {
                    viewModel.playMusic(R.raw.game_music)
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.releaseMusic()
        }
    }

    LaunchedEffect(uiState.isMuted) {
        viewModel.setMuteMusic(uiState.isMuted)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is GameUiEffect.NavigateToResultScreen -> {
                    goToResult()
                }
            }
        }
    }

    GameScreenContent(
        modifier = modifier,
        uiState = uiState,
        onAnswerSelected = { viewModel.onAnswerSelected(it) },
        onNextClicked = { viewModel.onNextClicked() },
        onToggleMusic = { viewModel.toggleMusic() }
    )

}

@Composable
fun GameScreenContent(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    onAnswerSelected: (Answer) -> Unit = {},
    onNextClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {

    BackgroundWrapper(backgroundId = R.drawable.game_background) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

                DescriptionBox(
                    modifier = Modifier.weight(1f),
                    textStyle = MainTestTheme.typography.heading,
                    descriptionString = stringResource(uiState.currentQuestion.questionResId)
                )

                Box(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    MusicToggleButton(
                        isMuted = uiState.isMuted,
                        onClick = onToggleMusic
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(modifier = Modifier.fillMaxSize()) {

                AnswersGroup(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .padding(end = 18.dp),
                    answers = uiState.currentQuestion.listOfAnswers,
                    selectedAnswer = uiState.selectedAnswer,
                    onAnswerSelected = { onAnswerSelected(it) }
                )

                Column(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    NextArrowButton(
                        onClick = onNextClicked,
                        isEnabled = uiState.selectedAnswer != null
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.game_questions),
                            style = MainTestTheme.typography.subText.copy(fontSize = 12.sp),
                            color = MainTestTheme.colors.primaryText.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "${uiState.questionNumber} / ${uiState.totalQuestions}",
                            style = MainTestTheme.typography.subText,
                            color = MainTestTheme.colors.primaryText.copy(alpha = 0.6f)
                        )
                    }
                }
            }

        }

    }

}

@Composable
fun NextArrowButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    val targetBackgroundColor by animateColorAsState(
        targetValue = if (isEnabled) MainTestTheme.colors.primaryElement else Color.DarkGray,
        animationSpec = tween(durationMillis = 300),
        label = "ArrowBgColor"
    )

    val targetArrowColor by animateColorAsState(
        targetValue = if (isEnabled) MainTestTheme.colors.primaryText else MainTestTheme.colors.secondaryText,
        animationSpec = tween(durationMillis = 300),
        label = "ArrowColor"
    )

    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(targetBackgroundColor)
            .border(
                width = 1.5.dp,
                color = MainTestTheme.colors.primaryBackground.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clickable(
                enabled = isEnabled,
                onClick = {
                    onClick()
                },
                indication = ripple(bounded = true, color = MainTestTheme.colors.primaryElement),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_next),
            contentDescription = "Next",
            tint = targetArrowColor,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewGameScreen() {
    MainTheme {
        GameScreenContent(
            uiState = GameUiState(
                currentQuestion = QuestionsGenerator().generateQuestions()[0]
            )
        )
    }
}