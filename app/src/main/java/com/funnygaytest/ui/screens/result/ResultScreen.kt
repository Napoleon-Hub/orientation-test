package com.funnygaytest.ui.screens.result

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.IconButton
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme

private const val DEVELOPER_URI =
    "https://play.google.com/store/apps/dev?id=6364243335711753284"
private const val MAX_POINTS: Double = 111.0
private const val SHARE_TEXT_TYPE = "text/plain"

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    viewModel: ResultViewModel = hiltViewModel(),
    goToStart: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

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

    LaunchedEffect(Unit) {
        viewModel.getReviewInfo()
        viewModel.refreshGameData()
    }

    LaunchedEffect(uiState.isMuted) {
        viewModel.setMuteMusic(uiState.isMuted)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ResultUiEffect.NavigateToStartScreen -> {
                    goToStart()
                }
            }
        }
    }

    val resultText = getResultText(uiState.points, context)

    ResultScreenContent(
        modifier = modifier,
        uiState = uiState,
        resultText = resultText,
        onRestartClicked = {
            viewModel.onRestartClicked()
        },
        onPayClicked = {
            activity?.let { viewModel.launchBillingFlow(it) }
        },
        onAnotherTestsClicked = {
            showAnotherApps(context)
        },
        onShareClicked = {
            share(context, resultText)
        },
        onRateClicked = {
            activity?.let { viewModel.rateUs(it) }
        },
        onToggleMusic = {
            viewModel.toggleMusic()
        }
    )

}

@Composable
fun ResultScreenContent(
    modifier: Modifier = Modifier,
    uiState: ResultUiState,
    resultText: String,
    onRestartClicked: () -> Unit = {},
    onPayClicked: () -> Unit = {},
    onAnotherTestsClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onRateClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    BackgroundWrapper(backgroundId = R.drawable.result_background) {

        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .padding(end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(R.string.result_title),
                    style = MainTestTheme.typography.heading.copy(fontSize = 24.sp),
                    color = MainTestTheme.colors.primaryText.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                DescriptionBox(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MainTestTheme.typography.heading,
                    descriptionString = resultText
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
            ) {

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    MusicToggleButton(
                        modifier = Modifier.align(Alignment.TopEnd),
                        isMuted = uiState.isMuted,
                        onClick = onToggleMusic
                    )
                }

                Column(
                    modifier = Modifier.weight(6f),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        onClick = onRestartClicked,
                        text = stringResource(R.string.result_button_restart)
                    )

                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        onClick = onPayClicked,
                        text = stringResource(R.string.result_button_pay)
                    )

                    MainButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        onClick = onAnotherTestsClicked,
                        text = stringResource(R.string.result_button_another_apps)
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        IconButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            iconId = R.drawable.ic_share,
                            onClick = onShareClicked
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(55.dp),
                            iconId = R.drawable.ic_rate_us,
                            onClick = onRateClicked,
                            enabled = uiState.isRateEnabled
                        )
                    }
                }

            }
        }
    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewResultScreen() {
    MainTheme {
        ResultScreenContent(
            uiState = ResultUiState(),
            resultText = stringResource(R.string.result_text_result_not_gay, 99)
        )
    }
}

private fun getResultText(points: Int, context: Context): String {
    val result = ((points.toDouble() / MAX_POINTS) * 100).toInt()
    return when {
        result < 25 -> context.getString(R.string.result_text_result_not_gay, result)
        result < 50 -> context.getString(R.string.result_text_result_little_gay, result)
        result < 75 -> context.getString(R.string.result_text_result_probably_gay, result)
        else -> context.getString(R.string.result_text_result_definitely_gay, result)
    }
}

private fun share(context: Context, resultText: String) {
    val shareText = context.getString(R.string.result_test_share, resultText)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = SHARE_TEXT_TYPE
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

private fun showAnotherApps(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, DEVELOPER_URI.toUri()))
    } catch (_: ActivityNotFoundException) {
    }
}