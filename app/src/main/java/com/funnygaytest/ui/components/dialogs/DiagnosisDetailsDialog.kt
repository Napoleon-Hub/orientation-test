package com.funnygaytest.ui.components.dialogs

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.funnygaytest.R
import com.funnygaytest.ui.components.buttons.MainButton
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun DiagnosisDetailsDialog(
    @DrawableRes iconRes: Int,
    title: String,
    titleStyle: TextStyle = MainTestTheme.typography.heading,
    description: String,
    descriptionStyle: TextStyle = MainTestTheme.typography.description,
    onDismiss: () -> Unit
) {
    val maxDialogHeight = LocalWindowInfo.current.containerSize.height.dp * 0.85f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .heightIn(max = maxDialogHeight)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MainTestTheme.colors.primaryElement,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .sizeIn(maxWidth = 75.dp, maxHeight = 75.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MainTestTheme.colors.primaryBackground.copy(alpha = 0.8f))
                        .border(
                            width = 1.dp,
                            color = MainTestTheme.colors.primaryText.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                var titleStyle by remember { mutableStateOf(titleStyle) }
                var isTitleReady by remember { mutableStateOf(false) }

                Text(
                    text = title,
                    style = titleStyle,
                    color = MainTestTheme.colors.primaryText,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent { if (isTitleReady) drawContent() },
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            titleStyle = titleStyle.copy(fontSize = titleStyle.fontSize * 0.9f)
                        } else {
                            isTitleReady = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                var descriptionStyle by remember { mutableStateOf(descriptionStyle) }
                var isDescriptionReady by remember { mutableStateOf(false) }

                Text(
                    text = description,
                    style = descriptionStyle,
                    color = MainTestTheme.colors.secondaryText,
                    textAlign = TextAlign.Start,
                    maxLines = 4,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent { if (isDescriptionReady) drawContent() },
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            descriptionStyle = descriptionStyle.copy(fontSize = descriptionStyle.fontSize * 0.9f)
                        } else {
                            isDescriptionReady = true
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                MainButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    text = stringResource(R.string.diagnosis_dialog_button_close),
                    onClick = onDismiss
                )
            }
        }
    }
}