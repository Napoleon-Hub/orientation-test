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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.funnygaytest.ui.components.buttons.LabBackButton
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.dialogs.DiagnosisDetailsDialog
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
                    viewModel.stopMusic()
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
    var selectedDiagnosisForDialog by remember { mutableStateOf<DiagnosisItemState?>(null) }

    selectedDiagnosisForDialog?.let { item ->
        DiagnosisDetailsDialog(
            iconRes = item.type.iconRes,
            title = stringResource(item.type.titleRes),
            description = stringResource(item.type.descriptionRes),
            onDismiss = { selectedDiagnosisForDialog = null }
        )
    }

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

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.55f)
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
                        text = stringResource(R.string.endings_diagnosis_title),
                        style = MainTestTheme.typography.heading,
                        color = MainTestTheme.colors.primaryText,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.endings_diagnosis_description),
                        style = MainTestTheme.typography.subText.copy(fontSize = 12.sp),
                        color = MainTestTheme.colors.secondaryText,
                        textAlign = TextAlign.Start
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyVerticalGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.endings) { item ->
                            DiagnosisGridItem(
                                item = item,
                                onItemClicked = {
                                    if(item.isUnlocked) selectedDiagnosisForDialog = item
                                }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.weight(0.45f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    StatsSection(
                        wins = uiState.wins,
                        loses = uiState.loses
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsSection(
    modifier: Modifier = Modifier,
    wins: Int,
    loses: Int
) {
    Column(
        modifier = modifier
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

        Column(
            modifier = Modifier.wrapContentWidth(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.endings_counter_wins, wins),
                style = MainTestTheme.typography.subText.copy(fontSize = 12.sp),
                color = MainTestTheme.colors.primaryText,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.endings_counter_loses, loses),
                style = MainTestTheme.typography.subText.copy(fontSize = 12.sp),
                color = MainTestTheme.colors.primaryText,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun DiagnosisGridItem(
    item: DiagnosisItemState,
    onItemClicked: () -> Unit
) {
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
                )
                .clickable(onClick = onItemClicked),
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
            maxLines = 3,
            minLines = 2
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
                    DiagnosisItemState(EndingType.WIN_100, true),
                    DiagnosisItemState(EndingType.WIN_66, true),
                    DiagnosisItemState(EndingType.WIN_33, true),
                    DiagnosisItemState(EndingType.WIN_1, true),
                    DiagnosisItemState(EndingType.LOSE_4, true),
                    DiagnosisItemState(EndingType.LOSE_8, true),
                    DiagnosisItemState(EndingType.LOSE_12, true),
                    DiagnosisItemState(EndingType.LOSE_16, true),
                    DiagnosisItemState(EndingType.LOSE_20, true),
                    DiagnosisItemState(EndingType.LOSE_PUSSY, true),
                    DiagnosisItemState(EndingType.ALL, true),
                    DiagnosisItemState(EndingType.DONATE, true),
                )
            )
        )
    }
}
