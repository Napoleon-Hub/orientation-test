package com.funnygaytest.screens.result

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.funnygaytest.R
import com.funnygaytest.ui.components.IconButton
import com.funnygaytest.ui.components.InfoDialog
import com.funnygaytest.ui.components.MainButton
import com.funnygaytest.ui.themes.MainTestTheme
import com.funnygaytest.utils.extentions.getActivity

private const val MAX_POINTS: Double = 111.0
private const val MARKET_URI = "market://details?id=com.funnygaytest"
private const val GOOGLE_PLAY_URI = "https://play.google.com/store/apps/details?id=com.funnygaytest"
private const val ANOTHER_GAMES_MARKET_URI = "market://dev?id=6364243335711753284"
private const val DEVELOPER_URI =
    "https://play.google.com/store/apps/dev?id=6364243335711753284"
private const val SHARE_TEXT_TYPE = "text/plain"

@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val productDetails by viewModel.productDetails.observeAsState()
    val context = LocalContext.current
    var showConnectionDialog by remember { mutableStateOf(false) }

    val resultText = generateResultText(uiState.points, context)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ResultUiEffect.NavigateToStartScreen -> {
//                    navController.navigate(NavRoutes.START) {
//                        popUpTo(0)
//                    }
                }

                is ResultUiEffect.ShowConnectionErrorDialog -> showConnectionDialog = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Текст результата (скроллируемый, если длинный)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.result_title),
                style = MainTestTheme.typography.heading
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = resultText,
                style = MainTestTheme.typography.description
            )

            // Кнопка доната (показывается только если биллинг загрузился)
            if (productDetails != null) {
                Spacer(modifier = Modifier.height(32.dp))
                MainButton(
                    onClick = { context.getActivity()?.let { viewModel.launchBillingFlow(it) } },
                    text = stringResource(id = R.string.result_button_pay)
                )
            }
        }

        // Блок кнопок действий
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(iconId = R.drawable.ic_share, onClick = { share(context, resultText) })
            IconButton(iconId = R.drawable.ic_rate_us, onClick = { rateUs(context) })
            //IconButton(iconResId = R.drawable.ic_another_apps) { showAnotherApps(context) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка рестарта
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
            onClick = { viewModel.onRestartClicked() },
            text = stringResource(id = R.string.result_button_restart)
        )
    }

    if (showConnectionDialog) {
        InfoDialog(
            title = stringResource(id = R.string.dialog_connection_error_title),
            desc = stringResource(id = R.string.dialog_connection_error_description),
            onDismiss = { showConnectionDialog = false }
        )
    }
}

// Функции-помощники
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
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = SHARE_TEXT_TYPE
    }
    context.startActivity(Intent.createChooser(sendIntent, null))
}

private fun rateUs(context: Context) {
    try {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(MARKET_URI + context.packageName)
            )
        )
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(GOOGLE_PLAY_URI + context.packageName)
            )
        )
    }
}

private fun showAnotherApps(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_URI)))
    } catch (e: ActivityNotFoundException) {
        // Fallback
    }
}