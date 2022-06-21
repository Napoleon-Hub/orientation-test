package com.funnygaytest.screens.start

import android.content.res.Configuration
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.funnygaytest.BuildConfig
import com.funnygaytest.GAME_SCREEN_NAME
import com.funnygaytest.R
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.ui.components.InfoDialog
import com.funnygaytest.ui.components.MainButton

private const val TEXT_TITLE_ID = "textTitle"
private const val TEXT_DESCRIPTION_ID = "textDescription"
private const val BUTTON_NEXT_ID = "buttonNext"
private const val TEXT_VERSION_ID = "textVersion"

@Composable
fun StartScreen(
    screenOrientation: Int,
    navController: NavController,
    viewModel: StartViewModel
) {

    val connectionErrorDialogShown = remember { mutableStateOf(false) }
    var descriptionText by remember { mutableStateOf("") }
    var nextButtonText by remember { mutableStateOf("") }

    val viewState = viewModel.uiState.collectAsState()

    when (viewState.value) {
        StartContract.State.ViewStateGameNotStarted -> {
            descriptionText = stringResource(R.string.start_description)
            nextButtonText = stringResource(R.string.start_button)
        }
        StartContract.State.ViewStateGameStarted -> {
            descriptionText = stringResource(R.string.start_description_continue)
            nextButtonText = stringResource(R.string.start_button_continue)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                StartContract.Effect.NavigateToGameScreen -> {
                    navController.navigate(GAME_SCREEN_NAME)
                }
                StartContract.Effect.ShowConnectionErrorDialog -> {
                    connectionErrorDialogShown.value = true
                }
            }
        }
    }

    BoxWithConstraints {

        val constraints = if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitConstraintSet()
        } else {
            landscapeConstraintSet()
        }

        ConstraintLayout(constraintSet = constraints, Modifier.fillMaxSize()) {

            Text(
                modifier = Modifier
                    .layoutId(TEXT_TITLE_ID)
                    .padding(horizontal = 24.dp),
                text = stringResource(R.string.app_name),
                style = MainTestTheme.typography.heading
            )

            Text(
                modifier = Modifier
                    .layoutId(TEXT_DESCRIPTION_ID)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                text = descriptionText,
                style = MainTestTheme.typography.description
            )

            MainButton(
                modifier = Modifier
                    .layoutId(BUTTON_NEXT_ID)
                    .height(66.dp),
                onClick = { viewModel.setEvent(StartContract.Event.OnNextClick) },
                text = nextButtonText
            )

            Text(
                modifier = Modifier.layoutId(TEXT_VERSION_ID),
                text = stringResource(R.string.app_version, BuildConfig.VERSION_NAME),
                style = MainTestTheme.typography.subText,
            )

            if (connectionErrorDialogShown.value) {
                InfoDialog(
                    title = stringResource(id = R.string.dialog_connection_error_title),
                    desc = stringResource(id = R.string.dialog_connection_error_description),
                    onDismiss = { connectionErrorDialogShown.value = false }
                )
            }

        }
    }

}

private fun portraitConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val textTitle = createRefFor(TEXT_TITLE_ID)
        val textDescription = createRefFor(TEXT_DESCRIPTION_ID)
        val buttonNext = createRefFor(BUTTON_NEXT_ID)
        val textVersion = createRefFor(TEXT_VERSION_ID)

        constrain(textTitle) {
            linkTo(start = parent.start, end = parent.end)
            linkTo(top = parent.top, bottom = buttonNext.top, bias = 0.1F)
        }

        constrain(textDescription) {
            linkTo(start = parent.start, end = parent.end)
            linkTo(
                top = textTitle.bottom, bottom = buttonNext.top,
                topMargin = 18.dp, bottomMargin = 18.dp, bias = 0F
            )
            height = Dimension.fillToConstraints
        }

        constrain(buttonNext) {
            linkTo(
                start = parent.start, end = parent.end,
                startMargin = 20.dp, endMargin = 20.dp
            )
            bottom.linkTo(textVersion.top, 12.dp)
            width = Dimension.fillToConstraints
        }

        constrain(textVersion) {
            linkTo(start = parent.start, end = parent.end)
            bottom.linkTo(parent.bottom, 24.dp)
        }
    }
}

private fun landscapeConstraintSet(): ConstraintSet {
    return ConstraintSet {
        val textTitle = createRefFor(TEXT_TITLE_ID)
        val textDescription = createRefFor(TEXT_DESCRIPTION_ID)
        val buttonNext = createRefFor(BUTTON_NEXT_ID)
        val textVersion = createRefFor(TEXT_VERSION_ID)
        val guideline = createGuidelineFromStart(0.65F)

        constrain(textTitle) {
            linkTo(start = parent.start, end = parent.end)
            top.linkTo(parent.top, margin = 12.dp)
        }

        constrain(textDescription) {
            linkTo(start = parent.start, end = guideline)
            linkTo(
                top = textTitle.bottom, bottom = parent.bottom,
                topMargin = 18.dp, bottomMargin = 22.dp
            )
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

        constrain(buttonNext) {
            linkTo(
                start = guideline, end = parent.end,
                startMargin = 12.dp, endMargin = 12.dp
            )
            linkTo(top = parent.top, bottom = parent.bottom)
            width = Dimension.fillToConstraints
        }

        constrain(textVersion) {
            linkTo(start = buttonNext.start, end = buttonNext.end)
            top.linkTo(buttonNext.bottom, 12.dp)
        }

    }
}