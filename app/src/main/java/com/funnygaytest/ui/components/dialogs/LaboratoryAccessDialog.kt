package com.funnygaytest.ui.components.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.funnygaytest.R
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.components.shrinkToFit
import com.funnygaytest.ui.components.verticalFadingEdges
import com.funnygaytest.ui.components.verticalScrollIndicator
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled
import com.funnygaytest.ui.utils.LandscapePreviews

private const val PrivacyPolicyUrl =
    "https://doc-hosting.flycricket.io/hardcore-gay-test-privacy-policy/d321abd6-7d19-4780-8079-b00305bb3aa9/privacy"
private const val MaxHeightFraction = 0.9f
private const val TextWeight = 1.7f
private const val ActionsWeight = 1f
private val MaxDialogWidth = 720.dp
private val DialogShape = RoundedCornerShape(24.dp)

@Composable
fun LaboratoryAccessDialog(
    onConsentAccepted: () -> Unit,
    onDecline: () -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        LaboratoryAccessContent(
            onConsentAccepted = onConsentAccepted,
            onDecline = onDecline
        )
    }
}

@Composable
private fun LaboratoryAccessContent(
    onConsentAccepted: () -> Unit,
    onDecline: () -> Unit
) {
    val dimens = LabTheme.dimens
    val uriHandler = LocalUriHandler.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = MaxDialogWidth * dimens.textScale)
                .heightIn(max = maxHeight * MaxHeightFraction),
            shape = DialogShape,
            color = LabTheme.colors.surface,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.padding(dimens.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(dimens.screenPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AccessTerms(modifier = Modifier.weight(TextWeight))

                AccessActions(
                    modifier = Modifier.weight(ActionsWeight),
                    onConsentAccepted = onConsentAccepted,
                    onDecline = onDecline,
                    onPrivacyPolicyClicked = { uriHandler.openUri(PrivacyPolicyUrl) }
                )
            }
        }
    }
}

@Composable
private fun AccessTerms(modifier: Modifier = Modifier) {
    val dimens = LabTheme.dimens
    val titleStyle = LabTheme.typography.heading.scaled(dimens.textScale)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
    ) {
        BasicText(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.access_dialog_title),
            style = titleStyle.copy(color = LabTheme.colors.textPrimary, textAlign = TextAlign.Start),
            maxLines = 1,
            softWrap = false,
            autoSize = shrinkToFit(titleStyle)
        )

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScrollIndicator(scrollState, color = LabTheme.colors.accent.copy(alpha = 0.6f))
                .verticalFadingEdges(scrollState, 24.dp)
                .verticalScroll(scrollState)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
        ) {
            Text(
                text = stringResource(R.string.access_dialog_warning),
                style = LabTheme.typography.description.scaled(dimens.textScale),
                color = LabTheme.colors.warning.copy(alpha = 0.8f),
                textAlign = TextAlign.Start
            )

            Text(
                text = stringResource(R.string.access_dialog_ad_description),
                style = LabTheme.typography.body.scaled(dimens.textScale),
                color = LabTheme.colors.textSecondary,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun AccessActions(
    modifier: Modifier = Modifier,
    onConsentAccepted: () -> Unit,
    onDecline: () -> Unit,
    onPrivacyPolicyClicked: () -> Unit
) {
    val dimens = LabTheme.dimens
    val secondaryStyle = LabTheme.typography.body.scaled(dimens.textScale)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimens.spacingSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.buttonHeight),
            text = stringResource(R.string.access_dialog_button_continue),
            textStyle = LabTheme.typography.button.scaled(dimens.textScale),
            onClick = onConsentAccepted
        )

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onDecline
        ) {
            Text(
                text = stringResource(R.string.access_dialog_button_finish),
                style = secondaryStyle,
                color = LabTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPrivacyPolicyClicked
        ) {
            Text(
                text = stringResource(R.string.access_dialog_privacy_policy),
                style = LabTheme.typography.caption.scaled(dimens.textScale)
                    .copy(textDecoration = TextDecoration.Underline),
                color = LabTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@LandscapePreviews
@Composable
private fun LaboratoryAccessDialogPreview() {
    LabTheme {
        LaboratoryAccessContent(onConsentAccepted = {}, onDecline = {})
    }
}
