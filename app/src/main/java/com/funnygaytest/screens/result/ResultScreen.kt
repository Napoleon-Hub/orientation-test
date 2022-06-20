package com.funnygaytest.screens.result

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.funnygaytest.R
import com.funnygaytest.START_SCREEN_NAME
import com.funnygaytest.ui.themes.GayTestTheme
import com.funnygaytest.ui.themes.components.IconButton
import com.funnygaytest.ui.themes.components.InfoDialog
import com.funnygaytest.ui.themes.components.MainButton
import com.funnygaytest.utils.extentions.getActivity

private const val CONSTRAINT_RESULT_BUTTONS_ID = "constraintResultButtons"
private const val CONSTRAINT_RESULT_TEXT_ID = "constraintResultText"
private const val TEXT_RESULT_TITLE_ID = "textResultTitle"
private const val TEXT_RESULT_DESCRIPTION_ID = "textResultDescription"
private const val TEXT_NOTICE_ID = "textNotice"
private const val BUTTON_RESTART_ID = "buttonRestart"
private const val BUTTON_ANOTHER_APPS_ID = "buttonAnotherApps"
private const val BUTTON_SHARE_ID = "buttonShare"
private const val BUTTON_PAY_ID = "buttonPay"
private const val BUTTON_RATE_US_ID = "buttonRateUs"

private const val MAX_POINTS: Double = 111.0

private const val SHARE_TEXT_TYPE = "text/plain"
private const val MARKET_URI = "market://details?id=com.funnygaytest"
private const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=com.funnygaytest"
private const val ANOTHER_GAMES_MARKET_URI = "market://dev?id=6364243335711753284"
private const val ANOTHER_GAMES_GOOGLE_PLAY_URI = "https://play.google.com/store/apps/dev?id=6364243335711753284"

@Composable
fun ResultScreen(
    screenOrientation: Int,
    navController: NavController,
    viewModel: ResultViewModel
) {

    val context = LocalContext.current
    var connectionErrorDialogShown by remember { mutableStateOf(false) }

    var noticeTextId by remember { mutableStateOf(R.string.result_text_notice) }
    var price by remember { mutableStateOf("") }
    var isPayButtonEnable by remember { mutableStateOf(false) }
    val resultText = generateResultText(viewModel.points, context)

    viewModel.skuDetails.observeForever { scuDetails ->
        if (scuDetails?.price != null) {
            noticeTextId = R.string.result_text_full
            price = scuDetails.price
            isPayButtonEnable = true
        } else {
            noticeTextId = R.string.result_text_notice
            isPayButtonEnable = false
        }
    }
    viewModel.refreshGameData()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ResultContract.Effect.ShowConnectionErrorDialog -> {
                    connectionErrorDialogShown = true
                }
                ResultContract.Effect.OpenShareActivity -> {
                    share(context, resultText)
                }
                ResultContract.Effect.OpenRateUsActivity -> {
                    rateUs(context)
                }
                ResultContract.Effect.OpenAnotherAppsActivity -> {
                    showAnotherApps(context)
                }
                ResultContract.Effect.NavigateToStartScreen -> {
                    navController.navigate(START_SCREEN_NAME) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    BoxWithConstraints {

        ConstraintLayout(
            constraintSet = constraintSetMain(),
            modifier = Modifier.fillMaxSize()
        ) {

            ConstraintLayout(
                modifier = Modifier
                    .layoutId(CONSTRAINT_RESULT_TEXT_ID)
                    .verticalScroll(rememberScrollState())
                    .padding(all = 10.dp),
                constraintSet = constraintSetText()
            ) {

                Text(
                    modifier = Modifier
                        .layoutId(TEXT_RESULT_TITLE_ID)
                        .padding(horizontal = 20.dp),
                    text = stringResource(id = R.string.result_title),
                    style = GayTestTheme.typography.heading
                )

                Text(
                    modifier = Modifier
                        .layoutId(TEXT_RESULT_DESCRIPTION_ID)
                        .padding(horizontal = 20.dp),
                    text = resultText,
                    style = GayTestTheme.typography.description.copy(fontSize = 18.sp)
                )

                Text(
                    modifier = Modifier
                        .layoutId(TEXT_NOTICE_ID)
                        .padding(horizontal = 20.dp),
                    text = if (price.isEmpty()) stringResource(id = noticeTextId)
                    else stringResource(id = noticeTextId, price),
                    style = GayTestTheme.typography.description.copy(fontSize = 14.sp)
                )

            }

            val constraintsButtons = if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
                portraitConstraintSetButtons()
            } else {
                landscapeConstraintSetButtons()
            }

            ConstraintLayout(
                modifier = Modifier.layoutId(CONSTRAINT_RESULT_BUTTONS_ID),
                constraintSet = constraintsButtons
            ) {


                MainButton(
                    modifier = Modifier.layoutId(BUTTON_RESTART_ID),
                    text = stringResource(id = R.string.result_button_restart),
                    onClick = { viewModel.setEvent(ResultContract.Event.OnRestartGameClick) }
                )

                MainButton(
                    modifier = Modifier.layoutId(BUTTON_ANOTHER_APPS_ID),
                    text = stringResource(id = R.string.result_button_another_apps),
                    onClick = { viewModel.setEvent(ResultContract.Event.OnAnotherAppsClick) }
                )

                IconButton(
                    modifier = Modifier.layoutId(BUTTON_SHARE_ID),
                    iconId = R.drawable.ic_share,
                    onClick = { viewModel.setEvent(ResultContract.Event.OnShareResultsClick) }
                )

                MainButton(
                    modifier = Modifier.layoutId(BUTTON_PAY_ID),
                    text = stringResource(id = R.string.result_button_pay),
                    onClick = {
                        val activity = context.getActivity()
                        if (activity != null) {
                            viewModel.setEvent(ResultContract.Event.OnPayClick(activity))
                        }
                    },
                    enabled = isPayButtonEnable
                )

                IconButton(
                    modifier = Modifier.layoutId(BUTTON_RATE_US_ID),
                    iconId = R.drawable.ic_rate_us,
                    onClick = { viewModel.setEvent(ResultContract.Event.OnRateUsClick) }
                )


            }

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

private fun constraintSetMain(): ConstraintSet {
    return ConstraintSet {
        val constraintResultText = createRefFor(CONSTRAINT_RESULT_TEXT_ID)
        val constraintResultButtons = createRefFor(CONSTRAINT_RESULT_BUTTONS_ID)

        constrain(constraintResultText) {
            linkTo(start = parent.start, end = parent.end, startMargin = 20.dp, endMargin = 20.dp)
            linkTo(
                top = parent.top,
                bottom = constraintResultButtons.top,
                topMargin = 20.dp,
                bottomMargin = 10.dp
            )
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
        }

        constrain(constraintResultButtons) {
            linkTo(start = parent.start, end = parent.end)
            bottom.linkTo(parent.bottom)
            width = Dimension.matchParent
            height = Dimension.fillToConstraints
        }
    }
}

private fun portraitConstraintSetButtons(): ConstraintSet {
    return ConstraintSet {
        val leftGuideline = createGuidelineFromStart(0.25f)
        val rightGuideline = createGuidelineFromEnd(0.25f)
        val centerGuideline = createGuidelineFromEnd(0.5f)
        val buttonRestart = createRefFor(BUTTON_RESTART_ID)
        val buttonAnotherApps = createRefFor(BUTTON_ANOTHER_APPS_ID)
        val buttonShare = createRefFor(BUTTON_SHARE_ID)
        val buttonPay = createRefFor(BUTTON_PAY_ID)
        val buttonRateUs = createRefFor(BUTTON_RATE_US_ID)

        constrain(buttonRestart) {
            linkTo(start = parent.start, end = centerGuideline, startMargin = 20.dp, endMargin = 4.dp)
            bottom.linkTo(parent.bottom, 28.dp)
            width = Dimension.fillToConstraints
        }

        constrain(buttonAnotherApps) {
            linkTo(start = centerGuideline, end = parent.end, startMargin = 4.dp, endMargin = 20.dp)
            bottom.linkTo(parent.bottom, 28.dp)
            width = Dimension.fillToConstraints
        }

        constrain(buttonShare) {
            linkTo(start = buttonRestart.start, end = leftGuideline, endMargin = 8.dp)
            linkTo(top = buttonPay.top, bottom = buttonPay.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

        constrain(buttonPay) {
            linkTo(start = leftGuideline, end = rightGuideline)
            bottom.linkTo(buttonRestart.top, 10.dp)
            height = Dimension.wrapContent
            width = Dimension.fillToConstraints
        }

        constrain(buttonRateUs) {
            linkTo(start = rightGuideline, end = buttonAnotherApps.end, startMargin = 8.dp)
            linkTo(top = buttonPay.top, bottom = buttonPay.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }
    }
}

private fun landscapeConstraintSetButtons(): ConstraintSet {
    return ConstraintSet {
        val startGuideline = createGuidelineFromStart(0.2f)
        val endGuideline = createGuidelineFromEnd(0.2f)
        val centerStartGuideline = createGuidelineFromStart(0.35f)
        val centerEndGuideline = createGuidelineFromEnd(0.35f)
        val centerGuideline = createGuidelineFromEnd(0.5f)
        val buttonRestart = createRefFor(BUTTON_RESTART_ID)
        val buttonAnotherApps = createRefFor(BUTTON_ANOTHER_APPS_ID)
        val buttonShare = createRefFor(BUTTON_SHARE_ID)
        val buttonPay = createRefFor(BUTTON_PAY_ID)
        val buttonRateUs = createRefFor(BUTTON_RATE_US_ID)

        constrain(buttonRestart) {
            linkTo(start = startGuideline, end = centerGuideline, endMargin = 4.dp)
            bottom.linkTo(parent.bottom, 28.dp)
            width = Dimension.fillToConstraints
        }

        constrain(buttonAnotherApps) {
            linkTo(start = centerGuideline, end = endGuideline, startMargin = 4.dp)
            bottom.linkTo(parent.bottom, 28.dp)
            width = Dimension.fillToConstraints
        }

        constrain(buttonShare) {
            linkTo(start = buttonRestart.start, end = centerStartGuideline, endMargin = 8.dp)
            linkTo(top = buttonPay.top, bottom = buttonPay.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

        constrain(buttonPay) {
            linkTo(start = centerStartGuideline, end = centerEndGuideline)
            bottom.linkTo(buttonRestart.top, 10.dp)
            height = Dimension.wrapContent
            width = Dimension.fillToConstraints
        }

        constrain(buttonRateUs) {
            linkTo(start = centerEndGuideline, end = buttonAnotherApps.end, startMargin = 8.dp)
            linkTo(top = buttonPay.top, bottom = buttonPay.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.fillToConstraints
        }

    }
}

private fun constraintSetText(): ConstraintSet {
    return ConstraintSet {
        val textResultTitle = createRefFor(TEXT_RESULT_TITLE_ID)
        val textResultDescription = createRefFor(TEXT_RESULT_DESCRIPTION_ID)
        val textNotice = createRefFor(TEXT_NOTICE_ID)

        constrain(textResultTitle) {
            linkTo(start = parent.start, end = parent.end, startMargin = 8.dp)
            top.linkTo(parent.top)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }

        constrain(textResultDescription) {
            linkTo(start = parent.start, end = parent.end, startMargin = 8.dp)
            top.linkTo(textResultTitle.bottom, 12.dp)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }

        constrain(textNotice) {
            linkTo(start = parent.start, end = parent.end, startMargin = 8.dp)
            top.linkTo(textResultDescription.bottom)
            height = Dimension.fillToConstraints
            width = Dimension.matchParent
        }
    }
}

private fun generateResultText(points: Int, context: Context): String {
    val result = ((points.toDouble() / MAX_POINTS) * 100).toInt()
    return when {
        result < 25 -> context.getString(R.string.result_text_result_not_gay, result)
        result < 50 -> context.getString(R.string.result_text_result_little_gay, result)
        result < 75 -> context.getString(R.string.result_text_result_probably_gay, result)
        else -> context.getString(R.string.result_text_result_definitely_gay, result)
    }
}

private fun share(context: Context, resultText: String) {
    val shareText = context.getString(R.string.result_test_share, resultText)
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = SHARE_TEXT_TYPE
    }

    context.startActivity(Intent.createChooser(sendIntent, null))
}

private fun rateUs(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URI)))
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PLAY_URI)))
    }
}

private fun showAnotherApps(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ANOTHER_GAMES_MARKET_URI)))
    } catch (e: ActivityNotFoundException) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ANOTHER_GAMES_GOOGLE_PLAY_URI)))
    }
}