package com.funnygaytest.ui.screens.connection

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme

@Composable
fun NoInternetScreen(
    viewModel: NoInternetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isMuted) {
        viewModel.setMuteMusic(uiState.isMuted)
    }

    NoInternetScreenContent(
        uiState = uiState,
        onToggleMusic = { viewModel.toggleMusic() }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun NoInternetScreenContent(
    uiState: NoInternetUiState,
    onToggleMusic: () -> Unit = {}
) {
    BackgroundWrapper(backgroundId = R.drawable.background_connection) {

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp, top = 12.dp)
        ) {
            MusicToggleButton(
                isMuted = uiState.isMuted,
                onClick = onToggleMusic
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.connection_no_internet_title),
                style = MainTestTheme.typography.heading.copy(fontSize = 22.sp),
                color = MainTestTheme.colors.primaryText.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Spacer(modifier = Modifier.weight(0.3f))
                DescriptionBox(
                    modifier = Modifier.weight(1f),
                    textStyle = MainTestTheme.typography.heading,
                    descriptionString = AnnotatedString(stringResource(R.string.connection_no_internet_description))
                )
                Spacer(modifier = Modifier.weight(0.3f))
            }
        }

    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewNoInternetScreen() {
    MainTheme {
        NoInternetScreenContent(uiState = NoInternetUiState())
    }
}