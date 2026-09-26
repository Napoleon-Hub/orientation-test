package com.funnygaytest.ui.screens.result

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.ScreenMusic
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.EndingCard
import com.funnygaytest.ui.components.buttons.IconButton
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.buttons.adaptiveButtonModifier
import com.funnygaytest.ui.components.shrinkToFit
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews
import com.funnygaytest.utils.enums.EndingType

private const val APP_URI =
    "https://play.google.com/store/apps/details?id=com.funnygaytest"
private const val DEVELOPER_URI =
    "https://play.google.com/store/apps/dev?id=6364243335711753284"

private const val ReportWeight = 0.7f
private const val ActionsWeight = 0.3f

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = hiltViewModel(),
    onStartScreen: () -> Unit,
    onFeedScreen: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val activity = LocalActivity.current

    ScreenMusic(R.raw.endings_music_new, viewModel)

    val state = uiState ?: return
    ResultScreenContent(
        modifier = modifier,
        uiState = state,
        isMuted = isMuted,
        onRestartClicked = dropUnlessResumed { onStartScreen() },
        onPayClicked = dropUnlessResumed { onFeedScreen() },
        onAnotherTestsClicked = dropUnlessResumed { showAnotherApps(context) },
        onShareClicked = dropUnlessResumed {
            share(context, state.healthLeft, state.lastQuestionNumber)
        },
        onRateClicked = dropUnlessResumed {
            activity?.let { viewModel.rateUs(it) }
        },
        onToggleMusic = viewModel::toggleMute
    )
}

@Composable
private fun ResultScreenContent(
    modifier: Modifier = Modifier,
    uiState: ResultUiState,
    isMuted: Boolean = false,
    onRestartClicked: () -> Unit = {},
    onPayClicked: () -> Unit = {},
    onAnotherTestsClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onRateClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    val dimens = LabTheme.dimens

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = R.drawable.background_result
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.screenPadding),
            horizontalArrangement = Arrangement.spacedBy(dimens.screenPadding)
        ) {
            ResultReport(
                modifier = Modifier
                    .weight(ReportWeight)
                    .fillMaxHeight(),
                uiState = uiState
            )

            ResultActions(
                modifier = Modifier
                    .weight(ActionsWeight)
                    .fillMaxHeight(),
                uiState = uiState,
                isMuted = isMuted,
                onRestartClicked = onRestartClicked,
                onPayClicked = onPayClicked,
                onAnotherTestsClicked = onAnotherTestsClicked,
                onShareClicked = onShareClicked,
                onRateClicked = onRateClicked,
                onToggleMusic = onToggleMusic
            )
        }
    }
}

@Composable
private fun ResultReport(
    modifier: Modifier = Modifier,
    uiState: ResultUiState
) {
    val dimens = LabTheme.dimens
    val titleStyle = LabTheme.typography.heading.copy(fontSize = 20.sp).scaled(dimens.textScale)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.2f))

        BasicText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(uiState.titleRes),
            style = titleStyle.copy(color = LabTheme.colors.textPrimary.copy(alpha = 0.7f)),
            maxLines = 2,
            autoSize = shrinkToFit(titleStyle)
        )

        Spacer(modifier = Modifier.height(dimens.spacingLarge))

        DescriptionBox(
            modifier = Modifier
                .weight(0.47f)
                .fillMaxWidth(),
            textStyle = LabTheme.typography.heading.copy(fontSize = 16.sp).scaled(dimens.textScale),
            descriptionString = AnnotatedString(stringResource(uiState.resultTextRes))
        )

        Box(
            modifier = Modifier
                .weight(0.33f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            val ending = uiState.currentEnding
            if (uiState.isNewEnding && ending != null) {
                EndingCard(
                    title = stringResource(ending.titleRes),
                    iconRes = ending.iconRes,
                    finalTextShown = uiState.isAllEndingsUnlocked
                )
            }
        }
    }
}

@Composable
private fun ResultActions(
    modifier: Modifier = Modifier,
    uiState: ResultUiState,
    isMuted: Boolean,
    onRestartClicked: () -> Unit,
    onPayClicked: () -> Unit,
    onAnotherTestsClicked: () -> Unit,
    onShareClicked: () -> Unit,
    onRateClicked: () -> Unit,
    onToggleMusic: () -> Unit
) {
    val dimens = LabTheme.dimens

    Column(modifier = modifier) {
        MusicToggleButton(
            modifier = Modifier.align(Alignment.End),
            isMuted = isMuted,
            onClick = onToggleMusic
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium, Alignment.CenterVertically)
        ) {
            val buttonModifier = adaptiveButtonModifier(dimens.buttonHeight)
            val buttonTextStyle = LabTheme.typography.button.scaled(dimens.textScale)

            MainButton(
                modifier = buttonModifier,
                onClick = onRestartClicked,
                textStyle = buttonTextStyle,
                text = stringResource(R.string.result_button_restart)
            )

            ActionCaption(text = stringResource(R.string.result_button_restart_description))

            MainButton(
                modifier = buttonModifier,
                onClick = onPayClicked,
                textStyle = buttonTextStyle,
                text = stringResource(R.string.result_button_pay)
            )

            MainButton(
                modifier = buttonModifier,
                onClick = onAnotherTestsClicked,
                textStyle = buttonTextStyle,
                text = stringResource(R.string.result_button_another_apps)
            )

            Row(
                modifier = buttonModifier,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
            ) {
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    iconId = R.drawable.ic_share,
                    onClick = onShareClicked
                )

                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    iconId = R.drawable.ic_rate_us,
                    onClick = onRateClicked,
                    enabled = uiState.isRateEnabled
                )
            }

            ActionCaption(text = stringResource(R.string.result_rate_description))
        }
    }
}

@Composable
private fun ActionCaption(text: String) {
    val style = LabTheme.typography.caption.scaled(LabTheme.dimens.textScale)
    BasicText(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = style,
        maxLines = 1,
        softWrap = false,
        autoSize = shrinkToFit(style)
    )
}

private class ResultUiStatePreviewProvider : PreviewParameterProvider<ResultUiState> {
    override val values = sequenceOf(
        ResultUiState(
            healthLeft = 100,
            lastQuestionNumber = 20,
            currentEnding = EndingType.WIN_100,
            isNewEnding = true
        ),
        ResultUiState(
            healthLeft = 0,
            lastQuestionNumber = 12,
            currentEnding = EndingType.LOSE_12,
            isNewEnding = true,
            isAllEndingsUnlocked = true
        ),
        ResultUiState(
            healthLeft = 0,
            lastQuestionNumber = 1,
            isRateEnabled = false
        )
    )
}

@LandscapePreviews
@Composable
private fun ResultScreenPreview(
    @PreviewParameter(ResultUiStatePreviewProvider::class) uiState: ResultUiState
) {
    LabTheme {
        ResultScreenContent(uiState = uiState)
    }
}

private fun share(context: Context, healthLeft: Int, lastQuestionNumber: Int) {
    val shareText = when (lastQuestionNumber) {
        20 if healthLeft > 0 -> context.getString(
            R.string.result_test_share_win,
            healthLeft,
            APP_URI
        )

        1 if healthLeft == 0 -> context.getString(
            R.string.result_test_share_permanent_lose,
            APP_URI
        )

        else -> context.getString(R.string.result_test_share_lose, lastQuestionNumber, APP_URI)
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

private fun showAnotherApps(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, DEVELOPER_URI.toUri()))
    } catch (_: ActivityNotFoundException) {
    }
}