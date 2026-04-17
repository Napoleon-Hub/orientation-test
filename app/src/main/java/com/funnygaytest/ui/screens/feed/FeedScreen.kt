package com.funnygaytest.ui.screens.feed

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
        R.drawable.feed_background_2
    } else {
        R.drawable.feed_background_1
    }

    BackgroundWrapper(backgroundId = currentBackgroundRes) {

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
                    text = stringResource(R.string.result_button_pay),
                    style = MainTestTheme.typography.heading,
                    color = MainTestTheme.colors.primaryText
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                DescriptionBox(
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MainTestTheme.typography.heading.copy(fontSize = 13.sp),
                    descriptionString = stringResource(
                        if (uiState.isDonated) R.string.feed_description_after_pay
                        else R.string.feed_description_before_pay
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.isDonated) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    MainButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(55.dp),
                        onClick = onPayClicked,
                        text = stringResource(R.string.feed_button)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            } else if (uiState.isDonateAchieveUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    EndingCard(
                        title = stringResource(id = EndingType.DONATE.titleRes),
                        iconRes = EndingType.DONATE.iconRes
                    )
                }
            }
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