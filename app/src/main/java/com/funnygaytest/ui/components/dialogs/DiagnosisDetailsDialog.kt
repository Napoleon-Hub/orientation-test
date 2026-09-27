package com.funnygaytest.ui.components.dialogs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
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

private const val MaxHeightFraction = 0.85f
private val MaxDialogWidth = 640.dp
private val DialogShape = RoundedCornerShape(24.dp)
private val IconShape = RoundedCornerShape(16.dp)

@Composable
fun DiagnosisDetailsDialog(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    onDismiss: () -> Unit
) {
    val dimens = LabTheme.dimens
    val titleStyle = LabTheme.typography.heading.scaled(dimens.textScale)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = null, indication = null, onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .clickable(interactionSource = null, indication = null, onClick = {})
                    .fillMaxWidth(0.9f)
                    .widthIn(max = MaxDialogWidth * dimens.textScale)
                    .heightIn(max = maxHeight * MaxHeightFraction),
                shape = DialogShape,
                color = LabTheme.colors.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(dimens.screenPadding),
                    horizontalArrangement = Arrangement.spacedBy(dimens.screenPadding)
                ) {
                    Box(
                        modifier = Modifier
                            .size(dimens.endingIconSize * 1.5f)
                            .clip(IconShape)
                            .background(LabTheme.colors.accent.copy(alpha = 0.8f))
                            .border(1.dp, LabTheme.colors.textPrimary.copy(alpha = 0.5f), IconShape)
                    ) {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        BasicText(
                            modifier = Modifier.fillMaxWidth(),
                            text = title,
                            style = titleStyle.copy(textAlign = TextAlign.Start),
                            maxLines = 1,
                            softWrap = false,
                            autoSize = shrinkToFit(titleStyle)
                        )

                        DialogDescription(
                            modifier = Modifier.weight(1f, fill = false),
                            text = description,
                            style = LabTheme.typography.description.scaled(dimens.textScale)
                        )

                        MainButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(dimens.buttonHeight),
                            text = stringResource(R.string.diagnosis_dialog_button_close),
                            textStyle = LabTheme.typography.button.scaled(dimens.textScale),
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogDescription(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle
) {
    val scrollState = rememberScrollState()
    Text(
        modifier = modifier
            .verticalScrollIndicator(scrollState, color = LabTheme.colors.accent.copy(alpha = 0.6f))
            .verticalFadingEdges(scrollState, 24.dp)
            .verticalScroll(scrollState)
            .padding(end = 12.dp),
        text = text,
        style = style,
        color = LabTheme.colors.textSecondary,
        textAlign = TextAlign.Start
    )
}
