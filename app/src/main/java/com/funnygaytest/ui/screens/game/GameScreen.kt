package com.funnygaytest.ui.screens.game

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
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
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.utils.helpers.generateNewGameRun

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    goToResult: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    viewModel.stopMusic()
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
                is GameUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.messageRes, Toast.LENGTH_LONG).show()
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
private fun GameScreenContent(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    onAnswerSelected: (Answer) -> Unit = {},
    onNextClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {

    val currentBackgroundRes = if (uiState.questionNumber <= 7) {
        R.drawable.background_game_1
    } else if (uiState.questionNumber <= 13) {
        R.drawable.background_game_2
    } else {
        R.drawable.background_game_3
    }

    BackgroundWrapper(backgroundId = currentBackgroundRes) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_health_title),
                    style = LabTheme.typography.caption,
                    color = LabTheme.colors.textPrimary.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )

                HealthBar(
                    currentHp = uiState.currentHp,
                    maxHp = uiState.maxHp
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.35f)
                ) {

                    DescriptionBox(
                        modifier = Modifier
                            .weight(0.85f)
                            .fillMaxHeight(),
                        textStyle = LabTheme.typography.heading,
                        descriptionString = AnnotatedString(stringResource(uiState.currentQuestion.questionResId))
                    )

                    Box(
                        modifier = Modifier
                            .weight(0.15f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        MusicToggleButton(
                            isMuted = uiState.isMuted,
                            onClick = onToggleMusic
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(0.65f)
                ) {

                    AnswersGroup(
                        modifier = Modifier
                            .weight(0.80f)
                            .fillMaxHeight()
                            .padding(end = 12.dp),
                        answers = uiState.currentQuestion.listOfAnswers,
                        selectedAnswer = uiState.selectedAnswer,
                        onAnswerSelected = { onAnswerSelected(it) }
                    )

                    Column(
                        modifier = Modifier
                            .weight(0.18f)
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        NextArrowButton(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .aspectRatio(1f)
                                .sizeIn(maxWidth = 56.dp, maxHeight = 56.dp),
                            onClick = onNextClicked,
                            isEnabled = uiState.selectedAnswer != null
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.game_questions),
                                style = LabTheme.typography.body.copy(fontSize = 12.sp),
                                color = LabTheme.colors.textPrimary.copy(alpha = 0.5f),
                                maxLines = 1
                            )
                            Text(
                                text = "${uiState.questionNumber} / ${uiState.totalQuestions}",
                                style = LabTheme.typography.body,
                                color = LabTheme.colors.textPrimary.copy(alpha = 0.6f),
                                maxLines = 1
                            )
                        }
                    }
                }

            }

        }

    }

}

@Composable
fun HealthBar(
    currentHp: Int,
    maxHp: Int,
    modifier: Modifier = Modifier
) {

    val progress = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "HpProgress"
    )

    val colors = LabTheme.colors
    val targetColor = when {
        progress > 0.8f -> colors.healthFull
        progress > 0.6f -> colors.healthHigh
        progress > 0.4f -> colors.healthMedium
        progress > 0.2f -> colors.healthLow
        else -> colors.healthCritical
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 600),
        label = "HpColor"
    )

    // Без зазора и точки-индикатора M3, чтобы полоса выглядела как раньше
    LinearProgressIndicator(
        progress = { animatedProgress },
        color = animatedColor,
        trackColor = colors.panel,
        strokeCap = StrokeCap.Butt,
        gapSize = 0.dp,
        drawStopIndicator = {},
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(CircleShape)
    )
}

@Composable
fun NextArrowButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    val targetBackgroundColor by animateColorAsState(
        targetValue = if (isEnabled) LabTheme.colors.surface else LabTheme.colors.disabled,
        animationSpec = tween(durationMillis = 300),
        label = "ArrowBgColor"
    )

    val targetArrowColor by animateColorAsState(
        targetValue = if (isEnabled) LabTheme.colors.textPrimary else LabTheme.colors.textSecondary,
        animationSpec = tween(durationMillis = 300),
        label = "ArrowColor"
    )

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(targetBackgroundColor)
            .border(
                width = 1.5.dp,
                color = LabTheme.colors.accent.copy(alpha = 0.4f),
                shape = CircleShape
            )
            .clickable(
                enabled = isEnabled,
                onClick = { onClick() },
                indication = ripple(bounded = true, color = LabTheme.colors.surface),
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_next),
            contentDescription = "Next",
            tint = targetArrowColor,
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

@Preview(widthDp = 800, heightDp = 450, locale = "ru")
@Composable
fun PreviewGameScreen() {
    LabTheme {
        GameScreenContent(
            uiState = GameUiState(
                currentQuestion = generateNewGameRun()[3],
                totalQuestions = 20,
                questionNumber = 8,
                currentHp = 20
            )
        )
    }
}