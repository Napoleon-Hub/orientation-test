package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LabTheme.colors.surface,
    rippleColor: Color = LabTheme.colors.accent,
    text: String? = null,
    textStyle: TextStyle = LabTheme.typography.button,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(color = rippleColor)) {
        OutlinedButton(
            modifier = modifier,
            onClick = {
                val time = System.currentTimeMillis()
                if (time - lastClickTime.longValue >= 500L) {
                    lastClickTime.longValue = time
                    onClick()
                }
            },
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = Color.Unspecified,
                disabledContainerColor = LabTheme.colors.disabled,
                disabledContentColor = Color.Unspecified
            ),
            border = BorderStroke(1.5.dp, LabTheme.colors.accent.copy(alpha = 0.4f))
        ) {

            text?.let {
                Text(
                    text = it,
                    modifier = Modifier.wrapContentHeight().drawWithContent {
                        if (readyToDraw) {
                            drawContent()
                        }
                    },
                    style = scaledTextStyle,
                    softWrap = false,
                    maxLines = 1,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.didOverflowWidth || textLayoutResult.didOverflowHeight) {
                            scaledTextStyle = scaledTextStyle.copy(
                                fontSize = scaledTextStyle.fontSize * 0.95
                            )
                        } else {
                            readyToDraw = true
                        }
                    }
                )
            }
        }
    }
}