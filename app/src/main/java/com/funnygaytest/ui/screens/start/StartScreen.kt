package com.funnygaytest.ui.screens.start

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.funnygaytest.BuildConfig
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.LanguageToggleButton
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.buttons.adaptiveButtonModifier
import com.funnygaytest.ui.components.dialogs.LanguageSelectionDialog
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews
import com.funnygaytest.ui.utils.ObserveAsEvents
import com.funnygaytest.utils.enums.AppLanguage

private const val MenuWeight = 1f
private const val MenuFreeSpaceWeight = 2f

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),
    onGameStart: () -> Unit,
    onEndingsShow: () -> Unit,
    onLoseResultShow: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.onResume()
        viewModel.playMusic(R.raw.start_music)
        onPauseOrDispose { viewModel.stopMusic() }
    }

    LaunchedEffect(uiState.isMuted) {
        viewModel.setMuteMusic(uiState.isMuted)
    }

    ObserveAsEvents(viewModel.uiEffect) { effect ->
        when (effect) {
            StartUiEffect.NavigateToGame -> onGameStart()
            StartUiEffect.NavigateToLoseResult -> onLoseResultShow()
        }
    }

    BackHandler(enabled = uiState.showDifficulty) {
        viewModel.onDifficultyDismissed()
    }

    StartScreenContent(
        modifier = modifier,
        uiState = uiState,
        onStartGameClicked = dropUnlessResumed { viewModel.onStartGameClicked() },
        onEndingsClicked = dropUnlessResumed { onEndingsShow() },
        onDifficultyClicked = viewModel::onDifficultyClicked,
        onEasyClicked = dropUnlessResumed { viewModel.onEasyDifficultySelected() },
        onHardClicked = viewModel::onHardDifficultySelected,
        onToggleMusic = viewModel::toggleMusic,
        onLanguageSelected = viewModel::onLanguageSelected
    )
}

@Composable
private fun StartScreenContent(
    modifier: Modifier = Modifier,
    uiState: StartUiState,
    onStartGameClicked: () -> Unit = {},
    onEndingsClicked: () -> Unit = {},
    onDifficultyClicked: () -> Unit = {},
    onEasyClicked: () -> Unit = {},
    onHardClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {},
    onLanguageSelected: (AppLanguage) -> Unit = {}
) {
    val dimens = LabTheme.dimens
    var showLanguageDialog by rememberSaveable { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.currentLanguage,
            onLanguageSelected = { language ->
                showLanguageDialog = false
                onLanguageSelected(language)
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = R.drawable.background_start
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f)
            ) {
                StartMenu(
                    modifier = Modifier
                        .weight(MenuWeight)
                        .fillMaxHeight(),
                    uiState = uiState,
                    onStartGameClicked = onStartGameClicked,
                    onEndingsClicked = onEndingsClicked,
                    onDifficultyClicked = onDifficultyClicked,
                    onEasyClicked = onEasyClicked,
                    onHardClicked = onHardClicked
                )

                Spacer(modifier = Modifier.weight(MenuFreeSpaceWeight))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f)
            ) {
                DescriptionBox(
                    modifier = Modifier
                        .weight(0.85f)
                        .align(Alignment.Bottom),
                    textStyle = LabTheme.typography.description.scaled(dimens.textScale),
                    descriptionString = AnnotatedString(
                        stringResource(
                            if (uiState.isGameStarted) R.string.start_description_continue
                            else R.string.start_description
                        )
                    )
                )
                Spacer(modifier = Modifier.weight(0.15f))
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimens.screenPadding / 2)
        ) {
            MusicToggleButton(
                isMuted = uiState.isMuted,
                onClick = onToggleMusic
            )

            LanguageToggleButton(
                currentLanguage = uiState.currentLanguage,
                onClick = { showLanguageDialog = true }
            )
        }
    }
}

@Composable
private fun StartMenu(
    modifier: Modifier = Modifier,
    uiState: StartUiState,
    onStartGameClicked: () -> Unit,
    onEndingsClicked: () -> Unit,
    onDifficultyClicked: () -> Unit,
    onEasyClicked: () -> Unit,
    onHardClicked: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = uiState.showDifficulty,
        label = "StartMenuTransition",
        transitionSpec = {
            val direction = if (targetState) SlideDirection.Up else SlideDirection.Down
            (fadeIn(tween(300)) + slideIntoContainer(direction, tween(300)))
                .togetherWith(fadeOut(tween(150)) + slideOutOfContainer(direction, tween(300)))
        }
    ) { showDifficulty ->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(
                space = LabTheme.dimens.spacingMedium,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showDifficulty) {
                DifficultyMenu(
                    isEasyLocked = uiState.wasPussyModeClicked,
                    onEasyClicked = onEasyClicked,
                    onHardClicked = onHardClicked
                )
            } else {
                MainMenu(
                    isGameStarted = uiState.isGameStarted,
                    onStartGameClicked = onStartGameClicked,
                    onDifficultyClicked = onDifficultyClicked,
                    onEndingsClicked = onEndingsClicked
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.MainMenu(
    isGameStarted: Boolean,
    onStartGameClicked: () -> Unit,
    onDifficultyClicked: () -> Unit,
    onEndingsClicked: () -> Unit
) {
    val buttonModifier = adaptiveButtonModifier(LabTheme.dimens.buttonHeight)
    val buttonTextStyle = LabTheme.typography.button.scaled(LabTheme.dimens.textScale)

    MainButton(
        modifier = buttonModifier,
        onClick = onStartGameClicked,
        textStyle = buttonTextStyle,
        text = stringResource(
            if (isGameStarted) R.string.start_button_continue else R.string.start_button
        )
    )

    MainButton(
        modifier = buttonModifier,
        onClick = onDifficultyClicked,
        textStyle = buttonTextStyle,
        text = stringResource(R.string.start_button_difficulty)
    )

    MainButton(
        modifier = buttonModifier,
        onClick = onEndingsClicked,
        textStyle = buttonTextStyle,
        text = stringResource(R.string.endings_title)
    )

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
        style = LabTheme.typography.body.scaled(LabTheme.dimens.textScale),
        color = LabTheme.colors.textSecondary.copy(alpha = 0.5f),
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun ColumnScope.DifficultyMenu(
    isEasyLocked: Boolean,
    onEasyClicked: () -> Unit,
    onHardClicked: () -> Unit
) {
    val buttonModifier = adaptiveButtonModifier(LabTheme.dimens.buttonHeight)
    val buttonTextStyle = LabTheme.typography.button.scaled(LabTheme.dimens.textScale)

    MainButton(
        modifier = buttonModifier,
        onClick = onEasyClicked,
        enabled = !isEasyLocked,
        textStyle = buttonTextStyle,
        text = stringResource(R.string.start_button_difficulty_easy)
    )

    if (isEasyLocked) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.start_button_difficulty_easy_gotcha),
            style = LabTheme.typography.caption.scaled(LabTheme.dimens.textScale),
            color = LabTheme.colors.error.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }

    MainButton(
        modifier = buttonModifier,
        onClick = onHardClicked,
        textStyle = buttonTextStyle,
        text = stringResource(R.string.start_button_difficulty_hard)
    )
}

private class StartUiStatePreviewProvider : PreviewParameterProvider<StartUiState> {
    override val values = sequenceOf(
        StartUiState(),
        StartUiState(isGameStarted = true, currentLanguage = AppLanguage.RU),
        StartUiState(showDifficulty = true, wasPussyModeClicked = true, currentLanguage = AppLanguage.DE)
    )
}

@LandscapePreviews
@Composable
private fun StartScreenPreview(
    @PreviewParameter(StartUiStatePreviewProvider::class) uiState: StartUiState
) {
    LabTheme {
        StartScreenContent(uiState = uiState)
    }
}
