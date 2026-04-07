package com.funnygaytest.screens.start

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.funnygaytest.BuildConfig
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.MainButton
import com.funnygaytest.ui.components.MusicToggleButton
import com.funnygaytest.ui.components.QuestionBox
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme

@Composable
fun StartScreen(
    modifier: Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),
    onGameStart: () -> Unit,
    onLoseResultShow: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_PAUSE -> {
                    viewModel.pauseMusic()
                }
                androidx.lifecycle.Lifecycle.Event.ON_RESUME -> {
                    if (!uiState.isMuted) {
                        viewModel.playMusic(R.raw.start_music)
                    }
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
                is StartUiEffect.NavigateToGame -> {
                    onGameStart()
                }
            }
        }
    }

    StartScreenContent(
        modifier = modifier,
        gameStartedState = uiState.isGameStarted,
        showDifficulty = uiState.showDifficulty,
        isMuted = uiState.isMuted,
        onStartGameClicked = { viewModel.onNextClicked() },
        onDifficultyGameClicked = { viewModel.onDifficultyClicked() },
        onEasyClicked = { onLoseResultShow() },
        onHardClicked = { viewModel.onDifficultySelected(true) },
        onToggleMusic = { viewModel.toggleMusic() }
    )

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StartScreenContent(
    modifier: Modifier = Modifier,
    gameStartedState: Boolean,
    showDifficulty: Boolean,
    isMuted: Boolean,
    onStartGameClicked: () -> Unit = {},
    onDifficultyGameClicked: () -> Unit = {},
    onEasyClicked: () -> Unit = {},
    onHardClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    BackgroundWrapper(backgroundId = R.drawable.start_background) {

        val playButtonText = if (gameStartedState) {
            stringResource(R.string.start_button_continue)
        } else {
            stringResource(R.string.start_button)
        }

        val bottomInfoRes = if (gameStartedState) {
            R.string.start_description_continue
        } else {
            R.string.start_description
        }

        Box(modifier = modifier.fillMaxSize()) {

            Box(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 12.dp)
            ) {
                MusicToggleButton(
                    isMuted = isMuted,
                    onClick = onToggleMusic
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 32.dp),
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {

                        AnimatedContent(
                            targetState = showDifficulty,
                            label = "ButtonTransition",
                            transitionSpec = {
                                if (!targetState) {
                                    (fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Down,
                                        animationSpec = tween(300)
                                    )).togetherWith(
                                        fadeOut(animationSpec = tween(150)) + slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Down,
                                            animationSpec = tween(300)
                                        )
                                    )
                                } else {
                                    (fadeIn(animationSpec = tween(300)) + slideIntoContainer(
                                        AnimatedContentTransitionScope.SlideDirection.Up,
                                        animationSpec = tween(300)
                                    )).togetherWith(
                                        fadeOut(animationSpec = tween(150)) + slideOutOfContainer(
                                            AnimatedContentTransitionScope.SlideDirection.Up,
                                            animationSpec = tween(300)
                                        )
                                    )
                                }
                            }
                        ) { targetShowDifficulty ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (!targetShowDifficulty) {
                                    MainButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        onClick = onStartGameClicked,
                                        text = playButtonText
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    MainButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        onClick = onDifficultyGameClicked,
                                        text = stringResource(R.string.start_button_difficulty)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                                        style = MainTestTheme.typography.subText,
                                        color = MainTestTheme.colors.secondaryText.copy(alpha = 0.5f),
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    MainButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        onClick = onEasyClicked,
                                        text = stringResource(R.string.start_button_difficulty_easy)
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    MainButton(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(55.dp),
                                        onClick = onHardClicked,
                                        text = stringResource(R.string.start_button_difficulty_hard)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(2f))

                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    QuestionBox(
                        modifier = Modifier.weight(0.85f),
                        textStyle = MainTestTheme.typography.description,
                        questionResId = bottomInfoRes
                    )
                    Spacer(modifier = Modifier.weight(0.15f))
                }

            }
        }
    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewStartScreen() {
    MainTheme {
        StartScreenContent(
            showDifficulty = false,
            gameStartedState = false,
            isMuted = false
        )
    }
}