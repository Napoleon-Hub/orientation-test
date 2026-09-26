package com.funnygaytest.ui.screens.connection

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.shrinkToFit
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews

private const val DescriptionWeight = 1f
private const val SideSpaceWeight = 0.3f

@Composable
fun NoInternetScreen(
    modifier: Modifier = Modifier,
    viewModel: NoInternetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    LaunchedEffect(Unit) {
        viewModel.onShown()
    }

    LaunchedEffect(uiState.isMuted) {
        viewModel.setMuteMusic(uiState.isMuted)
    }

    BackHandler {
        activity?.moveTaskToBack(true)
    }

    NoInternetScreenContent(
        modifier = modifier.pointerInput(Unit) {
            awaitPointerEventScope {
                while (true) {
                    awaitPointerEvent().changes.forEach { it.consume() }
                }
            }
        },
        uiState = uiState,
        onToggleMusic = viewModel::toggleMusic
    )
}

@Composable
private fun NoInternetScreenContent(
    modifier: Modifier = Modifier,
    uiState: NoInternetUiState,
    onToggleMusic: () -> Unit = {}
) {
    val dimens = LabTheme.dimens
    val titleStyle = LabTheme.typography.heading.copy(fontSize = 22.sp).scaled(dimens.textScale)

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = R.drawable.background_connection
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.connection_no_internet_title),
                style = titleStyle.copy(color = LabTheme.colors.textPrimary.copy(alpha = 0.7f)),
                maxLines = 1,
                softWrap = false,
                autoSize = shrinkToFit(titleStyle)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(SideSpaceWeight))
                DescriptionBox(
                    modifier = Modifier.weight(DescriptionWeight),
                    textStyle = LabTheme.typography.heading.scaled(dimens.textScale),
                    descriptionString = AnnotatedString(stringResource(R.string.connection_no_internet_description))
                )
                Spacer(modifier = Modifier.weight(SideSpaceWeight))
            }
        }

        MusicToggleButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(dimens.screenPadding / 2),
            isMuted = uiState.isMuted,
            onClick = onToggleMusic
        )
    }
}

@LandscapePreviews
@Composable
private fun NoInternetScreenPreview() {
    LabTheme {
        NoInternetScreenContent(uiState = NoInternetUiState())
    }
}
