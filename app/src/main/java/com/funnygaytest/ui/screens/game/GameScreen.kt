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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.funnygaytest.R
import com.funnygaytest.data.game.findQuestionById
import com.funnygaytest.model.Answer
import com.funnygaytest.ui.components.AnswersGroup
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews
import com.funnygaytest.ui.utils.ObserveAsEvents
import com.funnygaytest.ui.utils.ScreenMusic

private const val QuestionWeight = 0.35f
private const val AnswersWeight = 0.65f
private const val MainColumnWeight = 0.82f
private const val SideColumnWeight = 0.18f

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = hiltViewModel(),
    goToResult: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val showScrollHint by viewModel.showScrollHint.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ScreenMusic(R.raw.game_music, viewModel)

    ObserveAsEvents(viewModel.uiEffect) { effect ->
        when (effect) {
            GameUiEffect.NavigateToResultScreen -> goToResult()
            is GameUiEffect.ShowToast -> Toast.makeText(context, effect.messageRes, Toast.LENGTH_LONG).show()
        }
    }

    val state = uiState ?: return
    GameScreenContent(
        modifier = modifier,
        uiState = state,
        isMuted = isMuted,
        showScrollHint = showScrollHint,
        onAnswerSelected = viewModel::onAnswerSelected,
        onScrollHintShown = viewModel::onScrollHintShown,
        onNextClicked = viewModel::onNextClicked,
        onToggleMusic = viewModel::toggleMute
    )
}

@Composable
private fun GameScreenContent(
    modifier: Modifier = Modifier,
    uiState: GameUiState,
    isMuted: Boolean = false,
    showScrollHint: Boolean = false,
    onAnswerSelected: (Answer) -> Unit = {},
    onScrollHintShown: () -> Unit = {},
    onNextClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    val dimens = LabTheme.dimens
    val backgroundRes = when {
        uiState.questionNumber <= 7 -> R.drawable.background_game_1
        uiState.questionNumber <= 13 -> R.drawable.background_game_2
        else -> R.drawable.background_game_3
    }

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = backgroundRes
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.screenPadding, vertical = dimens.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
        ) {
            HealthSection(currentHp = uiState.currentHp, maxHp = uiState.maxHp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(QuestionWeight)
            ) {
                DescriptionBox(
                    modifier = Modifier
                        .weight(MainColumnWeight)
                        .fillMaxHeight(),
                    textStyle = LabTheme.typography.heading.scaled(dimens.textScale),
                    descriptionString = AnnotatedString(stringResource(uiState.currentQuestion.questionResId))
                )

                Box(
                    modifier = Modifier
                        .weight(SideColumnWeight)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    MusicToggleButton(
                        isMuted = isMuted,
                        onClick = onToggleMusic
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(AnswersWeight)
            ) {
                AnswersGroup(
                    modifier = Modifier
                        .weight(MainColumnWeight)
                        .fillMaxHeight(),
                    answers = uiState.currentQuestion.listOfAnswers,
                    selectedAnswer = uiState.selectedAnswer,
                    textStyle = LabTheme.typography.body.scaled(dimens.textScale),
                    showScrollHint = showScrollHint,
                    onAnswerSelected = onAnswerSelected,
                    onScrollHintShown = onScrollHintShown
                )

                Column(
                    modifier = Modifier
                        .weight(SideColumnWeight)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingSmall, Alignment.CenterVertically)
                ) {
                    NextArrowButton(
                        modifier = Modifier.size(dimens.actionButtonSize),
                        onClick = onNextClicked,
                        isEnabled = uiState.selectedAnswer != null
                    )

                    QuestionCounter(
                        questionNumber = uiState.questionNumber,
                        totalQuestions = uiState.totalQuestions
                    )
                }
            }
        }
    }
}

@Composable
private fun HealthSection(currentHp: Int, maxHp: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            text = stringResource(R.string.game_health_title),
            style = LabTheme.typography.caption.scaled(LabTheme.dimens.textScale),
            color = LabTheme.colors.textPrimary.copy(alpha = 0.5f)
        )

        HealthBar(currentHp = currentHp, maxHp = maxHp)
    }
}

@Composable
private fun QuestionCounter(questionNumber: Int, totalQuestions: Int) {
    val textScale = LabTheme.dimens.textScale
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.game_questions),
            style = LabTheme.typography.body.copy(fontSize = 12.sp).scaled(textScale),
            color = LabTheme.colors.textPrimary.copy(alpha = 0.5f),
            maxLines = 1
        )
        Text(
            text = "$questionNumber / $totalQuestions",
            style = LabTheme.typography.body.scaled(textScale),
            color = LabTheme.colors.textPrimary.copy(alpha = 0.6f),
            maxLines = 1
        )
    }
}

@Composable
private fun HealthBar(
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
private fun NextArrowButton(
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
            contentDescription = stringResource(R.string.game_next_button_description),
            tint = targetArrowColor,
            modifier = Modifier.fillMaxSize(0.5f)
        )
    }
}

private class GameUiStatePreviewProvider : PreviewParameterProvider<GameUiState> {
    private val longQuestion = findQuestionById("q_1_a")!!
    private val shortQuestion = findQuestionById("q_4_a")!!

    override val values = sequenceOf(
        GameUiState(
            currentQuestion = longQuestion,
            totalQuestions = 20,
            questionNumber = 1
        ),
        GameUiState(
            currentQuestion = longQuestion,
            selectedAnswer = longQuestion.listOfAnswers[2],
            totalQuestions = 20,
            questionNumber = 9,
            currentHp = 40
        ),
        GameUiState(
            currentQuestion = shortQuestion,
            totalQuestions = 20,
            questionNumber = 15,
            currentHp = 15
        )
    )
}

@LandscapePreviews
@Composable
private fun GameScreenPreview(
    @PreviewParameter(GameUiStatePreviewProvider::class) uiState: GameUiState
) {
    LabTheme {
        GameScreenContent(uiState = uiState)
    }
}
