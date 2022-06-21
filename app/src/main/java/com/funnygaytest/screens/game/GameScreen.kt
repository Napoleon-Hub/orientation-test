package com.funnygaytest.screens.game

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.funnygaytest.R
import com.funnygaytest.RESULT_SCREEN_NAME
import com.funnygaytest.ui.components.RadioGroup
import com.funnygaytest.ui.components.AdvertView
import com.funnygaytest.ui.components.InfoDialog
import com.funnygaytest.ui.components.MainButton
import kotlin.math.roundToInt

private const val RADIO_GROUP_ID = "radioGroup"
private const val CONSTRAINT_OUTSIDE_ID = "constraintOutside"
private const val BUTTON_NEXT_ID = "buttonNext"
private const val AD_VIEW_ID = "adView"


@Composable
fun GameScreen(
    screenOrientation: Int,
    navController: NavController,
    viewModel: GameViewModel
) {

    val context = LocalContext.current
    var connectionErrorDialogShown by remember { mutableStateOf(false) }

    var currentQuestion by remember { mutableStateOf(viewModel.lastQuestion.value) }
    viewModel.lastQuestion.observeForever { currentQuestion = it }


    val viewState = viewModel.uiState.collectAsState()
    var buttonText by remember { mutableStateOf(0) }
    var onButtonClick by remember { mutableStateOf({}) }
    when (viewState.value) {
        GameContract.State.ViewStatePlayGame -> {
            buttonText = R.string.game_button_next
            onButtonClick = { viewModel.setEvent(GameContract.Event.OnNextClick(false, context)) }
        }
        GameContract.State.ViewStateFinishGame -> {
            buttonText = R.string.game_button_finish
            onButtonClick = { viewModel.setEvent(GameContract.Event.OnNextClick(true, context)) }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                GameContract.Effect.NavigateToResultScreen -> {
                    navController.navigate(RESULT_SCREEN_NAME) {
                        popUpTo(0)
                    }
                }
                GameContract.Effect.ShowConnectionErrorDialog -> {
                    connectionErrorDialogShown = true
                }
                GameContract.Effect.ShowChooseAnswerToast -> {
                    Toast.makeText(
                        context,
                        context.getText(R.string.toast_choose_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    BoxWithConstraints {

        val constraintsMain = if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            portraitConstraintSetMain()
        } else {
            landscapeConstraintSetMain()
        }
        var containerWidth by remember { mutableStateOf<Int?>(null) }

        ConstraintLayout(
            constraintSet = constraintsMain,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged {
                    containerWidth = it.width
                }
        ) {
            val width = with(LocalDensity.current) {
                containerWidth?.let { containerWidth ->
                    (containerWidth / density).roundToInt()
                }
            }

            if (currentQuestion != null) {

                val scrollState = rememberScrollState()
                ConstraintLayout(
                    constraintSet = constraintSetOutsideCard(),
                    modifier = Modifier
                        .layoutId(CONSTRAINT_OUTSIDE_ID)
                        .verticalScroll(scrollState)
                ) {

                    LaunchedEffect(currentQuestion) {
                        scrollState.scrollTo(0)
                    }

                    RadioGroup(
                        modifier = Modifier.layoutId(RADIO_GROUP_ID),
                        question = currentQuestion!!,
                        countOfQuestions = viewModel.listOfQuestions.size,
                        previousSelectedAnswer = viewModel.selectedAnswer,
                        onAnswerClickRegister = {
                            viewModel.setEvent(GameContract.Event.OnAnswerClick(it))
                        }
                    )
                }
            }

            MainButton(
                modifier = Modifier
                    .layoutId(BUTTON_NEXT_ID)
                    .padding(horizontal = 16.dp)
                    .wrapContentWidth()
                    .height(66.dp),
                onClick = { onButtonClick() },
                text = stringResource(id = buttonText)
            )

            if (width != null) AdvertView(modifier = Modifier.layoutId(AD_VIEW_ID), width = width)

            if (connectionErrorDialogShown) {
                InfoDialog(
                    title = stringResource(id = R.string.dialog_connection_error_title),
                    desc = stringResource(id = R.string.dialog_connection_error_description),
                    onDismiss = { connectionErrorDialogShown = false }
                )
            }
        }

    }

}

private fun portraitConstraintSetMain(): ConstraintSet {
    return ConstraintSet {
        val constraintOutside = createRefFor(CONSTRAINT_OUTSIDE_ID)
        val buttonNext = createRefFor(BUTTON_NEXT_ID)
        val adView = createRefFor(AD_VIEW_ID)

        constrain(constraintOutside) {
            linkTo(
                start = parent.start,
                end = parent.end,
                startMargin = 30.dp,
                endMargin = 30.dp
            )
            linkTo(
                top = parent.top,
                bottom = buttonNext.top,
                topMargin = 30.dp,
                bottomMargin = 30.dp
            )
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

        constrain(buttonNext) {
            end.linkTo(constraintOutside.end)
            bottom.linkTo(adView.bottom, 90.dp)
        }

        constrain(adView) {
            linkTo(start = parent.start, end = parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

private fun landscapeConstraintSetMain(): ConstraintSet {
    return ConstraintSet {
        val constraintOutside = createRefFor(CONSTRAINT_OUTSIDE_ID)
        val buttonNext = createRefFor(BUTTON_NEXT_ID)
        val adView = createRefFor(AD_VIEW_ID)

        constrain(constraintOutside) {
            linkTo(
                start = parent.start,
                end = buttonNext.start,
                startMargin = 30.dp,
                endMargin = 30.dp
            )
            linkTo(
                top = parent.top,
                bottom = adView.bottom,
                topMargin = 30.dp,
                bottomMargin = 90.dp
            )
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

        constrain(buttonNext) {
            end.linkTo(parent.end, 16.dp)
            linkTo(top = parent.top, bottom = parent.bottom)
        }

        constrain(adView) {
            linkTo(start = parent.start, end = parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
}

private fun constraintSetOutsideCard(): ConstraintSet {
    return ConstraintSet {
        val radioGroup = createRefFor(RADIO_GROUP_ID)

        constrain(radioGroup) {
            linkTo(start = parent.start, end = parent.end, startMargin = 4.dp, endMargin = 4.dp)
            linkTo(top = parent.top, bottom = parent.bottom, topMargin = 4.dp, bottomMargin = 4.dp)
            height = Dimension.wrapContent
            width = Dimension.matchParent
        }

    }
}