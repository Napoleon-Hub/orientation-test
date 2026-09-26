package com.funnygaytest.ui.screens.endings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.funnygaytest.R
import com.funnygaytest.model.EndingType
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.buttons.LabBackButton
import com.funnygaytest.ui.components.buttons.MusicToggleButton
import com.funnygaytest.ui.components.dialogs.DiagnosisDetailsDialog
import com.funnygaytest.ui.components.verticalFadingEdges
import com.funnygaytest.ui.components.verticalScrollIndicator
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews
import com.funnygaytest.ui.utils.ScreenMusic

private const val VerdictsWeight = 0.62f
private const val StatsWeight = 0.38f
private val TileMinSize = 96.dp
private val FadeHeight = 32.dp
private val ScrollIndicatorGap = 12.dp
private val PanelShape = RoundedCornerShape(12.dp)
private val TileShape = RoundedCornerShape(16.dp)

@Composable
fun EndingsScreen(
    modifier: Modifier = Modifier,
    viewModel: EndingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()

    ScreenMusic(R.raw.endings_music_new, viewModel)

    EndingsScreenContent(
        modifier = modifier,
        uiState = uiState,
        isMuted = isMuted,
        onBackClicked = dropUnlessResumed { onBack() },
        onToggleMusic = viewModel::toggleMute
    )
}

@Composable
private fun EndingsScreenContent(
    modifier: Modifier = Modifier,
    uiState: EndingsUiState,
    isMuted: Boolean = false,
    onBackClicked: () -> Unit = {},
    onToggleMusic: () -> Unit = {}
) {
    val dimens = LabTheme.dimens
    var selectedEnding by rememberSaveable { mutableStateOf<EndingType?>(null) }

    selectedEnding?.let { ending ->
        DiagnosisDetailsDialog(
            iconRes = ending.iconRes,
            title = stringResource(ending.titleRes),
            description = stringResource(ending.descriptionRes),
            onDismiss = { selectedEnding = null }
        )
    }

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = R.drawable.background_endings
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.screenPadding, vertical = dimens.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
        ) {
            EndingsHeader(
                isMuted = isMuted,
                onBackClicked = onBackClicked,
                onToggleMusic = onToggleMusic
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
            ) {
                VerdictsPanel(
                    modifier = Modifier
                        .weight(VerdictsWeight)
                        .fillMaxHeight(),
                    endings = uiState.endings,
                    onEndingClicked = { item -> if (item.isUnlocked) selectedEnding = item.type }
                )

                StatsPanel(
                    modifier = Modifier.weight(StatsWeight),
                    wins = uiState.wins,
                    loses = uiState.loses
                )
            }
        }
    }
}

@Composable
private fun EndingsHeader(
    isMuted: Boolean,
    onBackClicked: () -> Unit,
    onToggleMusic: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LabTheme.dimens.spacingLarge)
    ) {
        LabBackButton(onClick = onBackClicked)

        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.endings_title),
            style = LabTheme.typography.heading.scaled(LabTheme.dimens.textScale),
            color = LabTheme.colors.textPrimary,
            textAlign = TextAlign.Start,
            maxLines = 1
        )

        MusicToggleButton(
            isMuted = isMuted,
            onClick = onToggleMusic
        )
    }
}

@Composable
private fun VerdictsPanel(
    modifier: Modifier = Modifier,
    endings: List<DiagnosisItemState>,
    onEndingClicked: (DiagnosisItemState) -> Unit
) {
    val dimens = LabTheme.dimens
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .labPanel()
            .padding(start = dimens.spacingLarge, top = dimens.spacingLarge, end = dimens.spacingSmall, bottom = dimens.spacingLarge)
    ) {
        Text(
            text = stringResource(R.string.endings_diagnosis_title),
            style = LabTheme.typography.heading.scaled(dimens.textScale),
            color = LabTheme.colors.textPrimary,
            textAlign = TextAlign.Start
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScrollIndicator(scrollState, color = LabTheme.colors.accent.copy(alpha = 0.6f))
                .verticalFadingEdges(scrollState, FadeHeight)
                .verticalScroll(scrollState)
                .padding(top = dimens.spacingSmall, end = ScrollIndicatorGap),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
        ) {
            Text(
                text = stringResource(R.string.endings_diagnosis_description),
                style = LabTheme.typography.body.copy(fontSize = 13.sp).scaled(dimens.textScale),
                color = LabTheme.colors.textPrimary.copy(alpha = 0.85f),
                textAlign = TextAlign.Start
            )

            EndingsGrid(endings = endings, onEndingClicked = onEndingClicked)
        }
    }
}

@Composable
private fun EndingsGrid(
    endings: List<DiagnosisItemState>,
    onEndingClicked: (DiagnosisItemState) -> Unit
) {
    val dimens = LabTheme.dimens
    val spacing = dimens.spacingMedium

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val minTileSize = TileMinSize * dimens.textScale
        val columns = ((maxWidth + spacing) / (minTileSize + spacing)).toInt().coerceAtLeast(2)

        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            endings.chunked(columns).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    rowItems.forEach { item ->
                        EndingTile(
                            modifier = Modifier.weight(1f),
                            item = item,
                            onClick = { onEndingClicked(item) }
                        )
                    }
                    repeat(columns - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EndingTile(
    modifier: Modifier = Modifier,
    item: DiagnosisItemState,
    onClick: () -> Unit
) {
    val colors = LabTheme.colors
    val textScale = LabTheme.dimens.textScale

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LabTheme.dimens.spacingSmall)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(TileShape)
                .background(if (item.isUnlocked) colors.accent.copy(alpha = 0.8f) else colors.panelStrong)
                .border(
                    width = 1.dp,
                    color = if (item.isUnlocked) colors.textPrimary.copy(alpha = 0.5f)
                    else colors.accent.copy(alpha = 0.25f),
                    shape = TileShape
                )
                .clickable(enabled = item.isUnlocked, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (item.isUnlocked) {
                Image(
                    painter = painterResource(item.type.iconRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.ic_lock),
                    contentDescription = null,
                    tint = colors.textSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxSize(0.3f)
                )
            }
        }

        Text(
            text = stringResource(if (item.isUnlocked) item.type.titleRes else R.string.endings_locked_title),
            style = LabTheme.typography.body.copy(fontSize = 11.sp).scaled(textScale),
            color = if (item.isUnlocked) colors.textPrimary else colors.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 3
        )
    }
}

@Composable
private fun StatsPanel(
    modifier: Modifier = Modifier,
    wins: Int,
    loses: Int
) {
    val dimens = LabTheme.dimens
    val statStyle = LabTheme.typography.body.copy(fontSize = 12.sp).scaled(dimens.textScale)

    Column(
        modifier = modifier
            .labPanel()
            .padding(dimens.spacingLarge),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingSmall)
    ) {
        Text(
            text = stringResource(R.string.endings_stats_title),
            style = LabTheme.typography.heading.scaled(dimens.textScale),
            color = LabTheme.colors.textPrimary,
            textAlign = TextAlign.Start
        )
        Text(
            text = stringResource(R.string.endings_counter_wins, wins),
            style = statStyle,
            color = LabTheme.colors.textPrimary,
            textAlign = TextAlign.Start
        )
        Text(
            text = stringResource(R.string.endings_counter_loses, loses),
            style = statStyle,
            color = LabTheme.colors.textPrimary,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun Modifier.labPanel(): Modifier = this
    .background(LabTheme.colors.panelOpaque, PanelShape)
    .border(1.dp, LabTheme.colors.accent.copy(alpha = 0.3f), PanelShape)

private class EndingsUiStatePreviewProvider : PreviewParameterProvider<EndingsUiState> {
    override val values = sequenceOf(
        EndingsUiState(
            wins = 3,
            loses = 100,
            endings = EndingType.entries.mapIndexed { index, type ->
                DiagnosisItemState(type, isUnlocked = index % 3 != 2)
            }
        ),
        EndingsUiState(
            endings = EndingType.entries.map { DiagnosisItemState(it, isUnlocked = false) }
        )
    )
}

@LandscapePreviews
@Composable
private fun EndingsScreenPreview(
    @PreviewParameter(EndingsUiStatePreviewProvider::class) uiState: EndingsUiState
) {
    LabTheme {
        EndingsScreenContent(uiState = uiState)
    }
}
