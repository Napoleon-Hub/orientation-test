package com.funnygaytest.ui.screens.feed

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.EndingCard
import com.funnygaytest.ui.components.buttons.LabBackButton
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.components.verticalFadingEdges
import com.funnygaytest.ui.components.verticalScrollIndicator
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews
import com.funnygaytest.utils.enums.EndingType

private const val TransitionDurationMs = 1000
private const val LevelFillDurationMs = 1500
private const val EmptyLevel = 0.15f
private const val EndingCardWeight = 0.67f
private val FadeHeight = 32.dp
private val ScrollIndicatorGap = 12.dp

@Composable
fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current

    FeedScreenContent(
        modifier = modifier,
        uiState = uiState,
        onPayClicked = dropUnlessResumed {
            activity?.let { viewModel.launchBillingFlow(it) }
        },
        onBackClicked = dropUnlessResumed { onBack() }
    )
}

@Composable
private fun FeedScreenContent(
    modifier: Modifier = Modifier,
    uiState: FeedUiState,
    onPayClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    val dimens = LabTheme.dimens

    BackgroundWrapper(
        modifier = modifier,
        backgroundId = if (uiState.isDonated) R.drawable.background_feed_2 else R.drawable.background_feed_1
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.screenPadding, vertical = dimens.spacingLarge),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.screenPadding)
            ) {
                LabBackButton(onClick = onBackClicked)
                CucumberLevelIndicator(
                    modifier = Modifier.weight(1f),
                    isFilled = uiState.isDonated
                )
            }

            Crossfade(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                targetState = uiState.isDonated,
                animationSpec = tween(TransitionDurationMs),
                label = "FeedContent"
            ) { donated ->
                if (donated) {
                    AfterDonationContent(showEndingCard = uiState.isDonateAchieveUnlocked)
                } else {
                    BeforeDonationContent(onPayClicked = onPayClicked)
                }
            }
        }
    }
}

@Composable
private fun BeforeDonationContent(onPayClicked: () -> Unit) {
    val dimens = LabTheme.dimens

    val labReport = buildAnnotatedString {
        listOf(
            R.string.feed_description_no_donate_1,
            R.string.feed_description_no_donate_2,
            R.string.feed_description_no_donate_3,
            R.string.feed_description_no_donate_4
        ).forEachIndexed { index, resId ->
            if (index > 0) append("\n\n")
            append("• ")
            append(stringResource(resId))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
    ) {
        ScrollableReport(
            modifier = Modifier.weight(1f),
            title = stringResource(R.string.feed_title_no_donate),
            report = labReport,
            reportStyle = LabTheme.typography.heading.copy(fontSize = 14.sp).scaled(dimens.textScale),
            reportAlign = TextAlign.Start
        )

        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.buttonHeight),
            onClick = onPayClicked,
            textStyle = LabTheme.typography.button.scaled(dimens.textScale),
            text = stringResource(R.string.feed_button)
        )
    }
}

@Composable
private fun AfterDonationContent(showEndingCard: Boolean) {
    val dimens = LabTheme.dimens

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
    ) {
        ScrollableReport(
            modifier = Modifier.weight(1f, fill = false),
            title = stringResource(R.string.feed_title_donate),
            report = AnnotatedString(stringResource(R.string.feed_description_donate)),
            reportStyle = LabTheme.typography.description.scaled(dimens.textScale),
            reportAlign = TextAlign.Center
        )

        if (showEndingCard) {
            val appearance = remember { MutableTransitionState(false).apply { targetState = true } }
            Row(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    modifier = Modifier.weight(EndingCardWeight),
                    visibleState = appearance,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(TransitionDurationMs))
                ) {
                    EndingCard(
                        title = stringResource(EndingType.DONATE.titleRes),
                        iconRes = EndingType.DONATE.iconRes
                    )
                }
                Spacer(modifier = Modifier.weight(1f - EndingCardWeight))
            }
        }
    }
}

@Composable
private fun ScrollableReport(
    modifier: Modifier = Modifier,
    title: String,
    report: AnnotatedString,
    reportStyle: TextStyle,
    reportAlign: TextAlign
) {
    val dimens = LabTheme.dimens
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScrollIndicator(scrollState, color = LabTheme.colors.accent.copy(alpha = 0.6f))
            .verticalFadingEdges(scrollState, FadeHeight)
            .verticalScroll(scrollState)
            .padding(end = ScrollIndicatorGap),
        verticalArrangement = Arrangement.spacedBy(dimens.spacingLarge)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = title,
            style = LabTheme.typography.heading.copy(fontSize = 20.sp).scaled(dimens.textScale),
            color = LabTheme.colors.textPrimary
        )

        DescriptionBox(
            modifier = Modifier.fillMaxWidth(),
            textStyle = reportStyle,
            textAlign = reportAlign,
            descriptionString = report
        )
    }
}

@Composable
private fun CucumberLevelIndicator(
    modifier: Modifier = Modifier,
    isFilled: Boolean
) {
    val colors = LabTheme.colors
    val level by animateFloatAsState(
        targetValue = if (isFilled) 1f else EmptyLevel,
        animationSpec = tween(LevelFillDurationMs),
        label = "CucumberLevel"
    )
    val levelColor by animateColorAsState(
        targetValue = if (isFilled) colors.success else colors.error,
        animationSpec = tween(LevelFillDurationMs),
        label = "CucumberColor"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(LabTheme.dimens.spacingSmall)
    ) {
        Crossfade(
            targetState = isFilled,
            animationSpec = tween(TransitionDurationMs),
            label = "CucumberStatus"
        ) { filled ->
            Text(
                text = stringResource(if (filled) R.string.feed_status_donate else R.string.feed_status_no_donate),
                style = LabTheme.typography.caption.scaled(LabTheme.dimens.textScale),
                color = levelColor
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .border(1.dp, colors.textPrimary.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(level)
                    .background(levelColor.copy(alpha = 0.7f), RoundedCornerShape(2.dp))
            )
        }
    }
}

private class FeedUiStatePreviewProvider : PreviewParameterProvider<FeedUiState> {
    override val values = sequenceOf(
        FeedUiState(),
        FeedUiState(isDonated = true, isDonateAchieveUnlocked = true)
    )
}

@LandscapePreviews
@Composable
private fun FeedScreenPreview(
    @PreviewParameter(FeedUiStatePreviewProvider::class) uiState: FeedUiState
) {
    LabTheme {
        FeedScreenContent(uiState = uiState)
    }
}
