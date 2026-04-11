package com.funnygaytest.ui.screens.endings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme
import com.funnygaytest.utils.enums.EndingType

@Composable
fun EndingsScreen(
    viewModel: EndingsViewModel = hiltViewModel(),
    onBack: () -> Unit
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
                    viewModel.playMusic(R.raw.endings_music)
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

    EndingsScreenContent(
        uiState = uiState,
        onBackClicked = onBack,
        onToggleMusic = { viewModel.toggleMusic() }
    )

}

@Composable
private fun EndingsScreenContent(
    uiState: EndingsUiState,
    onBackClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    BackgroundWrapper(backgroundId = R.drawable.endings_background) {

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
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabBackButton(onClick = onBackClicked)

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.endings_title),
                    style = MainTestTheme.typography.heading,
                    color = MainTestTheme.colors.primaryText
                )
            }

            StatsSection(uiState.wins, uiState.loses)

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                contentPadding = PaddingValues(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.endings) { item ->
                    EndingGridItem(item)
                }
            }
        }
    }
}

@Composable
private fun StatsSection(wins: Int, loses: Int) {
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .background(
                Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = MainTestTheme.colors.primaryBackground.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.endings_stats_title),
            style = MainTestTheme.typography.heading,
            color = MainTestTheme.colors.primaryText
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column (modifier = Modifier.wrapContentWidth(), verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.endings_counter_wins, wins),
                style = MainTestTheme.typography.subText,
                color = MainTestTheme.colors.primaryText
            )
            Text(
                text = stringResource(R.string.endings_counter_loses, loses),
                style = MainTestTheme.typography.subText,
                color = MainTestTheme.colors.primaryText
            )
        }
    }
}

@Composable
private fun EndingGridItem(item: EndingItemState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MainTestTheme.colors.primaryBackground.copy(alpha = 0.8f))
                .border(
                    width = 1.dp,
                    color = if (item.isUnlocked) MainTestTheme.colors.primaryText.copy(alpha = 0.5f)
                    else MainTestTheme.colors.secondaryText.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.isUnlocked) {
                Image(
                    painter = painterResource(id = item.type.iconRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "???",
                    style = MainTestTheme.typography.heading,
                    color = MainTestTheme.colors.secondaryText.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (item.isUnlocked) stringResource(item.type.titleRes)
            else stringResource(R.string.endings_locked_title),
            style = MainTestTheme.typography.subText.copy(fontSize = 11.sp),
            color = if (item.isUnlocked) MainTestTheme.colors.primaryText
            else MainTestTheme.colors.secondaryText.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2
        )
    }
}

@Composable
private fun LabBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MainTestTheme.colors.primaryBackground.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = MainTestTheme.colors.secondaryText,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MainTestTheme.colors.primaryText.copy(alpha = 0.8f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewEndingsScreen() {
    MainTheme {
        EndingsScreenContent(
            uiState = EndingsUiState(
                wins = 3,
                loses = 100,
                endings = listOf(
                    EndingItemState(EndingType.WIN_100, true),
                    EndingItemState(EndingType.WIN_66, true),
                    EndingItemState(EndingType.WIN_33, false),
                    EndingItemState(EndingType.WIN_1, true),
                    EndingItemState(EndingType.LOSE_4, true),
                    EndingItemState(EndingType.LOSE_8, false),
                    EndingItemState(EndingType.LOSE_12, false),
                    EndingItemState(EndingType.LOSE_16, true),
                    EndingItemState(EndingType.LOSE_20, true),
                    EndingItemState(EndingType.LOSE_PUSSY, true),
                    EndingItemState(EndingType.ALL, false)
                )
            )
        )
    }
}
