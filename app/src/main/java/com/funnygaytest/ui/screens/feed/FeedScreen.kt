package com.funnygaytest.ui.screens.feed

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.funnygaytest.R
import com.funnygaytest.ui.components.BackgroundWrapper
import com.funnygaytest.ui.components.DescriptionBox
import com.funnygaytest.ui.components.EndingCard
import com.funnygaytest.ui.components.buttons.LabBackButton
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.themes.LabError
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.themes.MainTheme
import com.funnygaytest.utils.enums.EndingType

@Composable
fun FeedScreen(
    viewModel: FeedViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val activity = context as? Activity

    FeedScreenContent(
        uiState = uiState,
        onPayClicked = {
            activity?.let { viewModel.launchBillingFlow(it) }
        },
        onBackClicked = onBack
    )

}

@Composable
private fun FeedScreenContent(
    uiState: FeedUiState,
    onPayClicked: () -> Unit = {},
    onBackClicked: () -> Unit = {}
) {
    val currentBackgroundRes = if (uiState.isDonated) {
        R.drawable.background_feed_2
    } else {
        R.drawable.background_feed_1
    }

    BackgroundWrapper(backgroundId = currentBackgroundRes) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LabBackButton(onClick = onBackClicked)
                Crossfade(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    targetState = uiState.isDonated,
                    animationSpec = tween(1000)
                ) { donated ->
                    CucumberLevelIndicator(isFilled = donated)
                }
            }

            Crossfade(targetState = uiState.isDonated, animationSpec = tween(1000)) { donated ->
                if (donated) {
                    AfterDonationContent(uiState)
                } else {
                    BeforeDonationContent(onPayClicked = onPayClicked)
                }
            }
        }

    }
}

@Composable
private fun BeforeDonationContent(onPayClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.feed_title_no_donate),
                style = MainTestTheme.typography.heading.copy(fontSize = 20.sp),
                color = MainTestTheme.colors.primaryText
            )
            Spacer(Modifier.height(16.dp))

            val combinedLabReport = buildAnnotatedString {
                append("• ")
                append(stringResource(R.string.feed_description_no_donate_1))
                append("\n\n• ")
                append(stringResource(R.string.feed_description_no_donate_2))
                append("\n\n• ")
                append(stringResource(R.string.feed_description_no_donate_3))
                append("\n\n• ")
                append(stringResource(R.string.feed_description_no_donate_4))
            }

            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
                DescriptionBox(
                    modifier = Modifier.wrapContentSize(),
                    textStyle = MainTestTheme.typography.heading.copy(fontSize = 14.sp),
                    textAlign = TextAlign.Start,
                    descriptionString = combinedLabReport
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            onClick = onPayClicked,
            text = stringResource(R.string.feed_button)
        )
    }
}

@Composable
private fun AfterDonationContent(uiState: FeedUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
    ) {
        Column {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.feed_title_donate),
                style = MainTestTheme.typography.heading.copy(fontSize = 20.sp),
                color = MainTestTheme.colors.primaryText
            )

            Spacer(Modifier.height(16.dp))

            DescriptionBox(
                modifier = Modifier.wrapContentSize(),
                textStyle = MainTestTheme.typography.description,
                descriptionString = AnnotatedString(stringResource(R.string.feed_description_donate))
            )
        }

        if (uiState.isDonateAchieveUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Row {
                    AnimatedVisibility(
                        modifier = Modifier.weight(0.67f),
                        visible = true,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = 1000)
                        ),
                        exit = fadeOut()
                    ) {
                        EndingCard(
                            title = stringResource(id = EndingType.DONATE.titleRes),
                            iconRes = EndingType.DONATE.iconRes
                        )
                    }
                    Spacer(Modifier.weight(0.33f))
                }
            }
        }

    }
}

@Composable
fun CucumberLevelIndicator(
    isFilled: Boolean
) {
    val level by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0.15f,
        animationSpec = tween(1500)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(if (isFilled) R.string.feed_status_donate else R.string.feed_status_no_donate),
            style = MainTestTheme.typography.noteText,
            color = if (isFilled) Color.Green else LabError
        )
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .border(1.dp, MainTestTheme.colors.primaryText.copy(0.3f), RoundedCornerShape(4.dp))
                .padding(2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(level)
                    .background(
                        if (isFilled) Color.Green.copy(0.7f) else Color.Red.copy(0.7f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Preview(widthDp = 720, heightDp = 500)
@Composable
fun PreviewEndingsScreen() {
    MainTheme {
        FeedScreenContent(
            uiState = FeedUiState()
        )
    }
}