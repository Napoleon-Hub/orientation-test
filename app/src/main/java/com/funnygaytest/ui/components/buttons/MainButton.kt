package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.ripple
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
import androidx.compose.ui.unit.sp
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MainTestTheme.colors.primaryElement,
    rippleColor: Color = MainTestTheme.colors.primaryElement,
    text: String? = null,
    textStyle: TextStyle = MainTestTheme.typography.buttonText,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    val customRipple = ripple(
        color = rippleColor,
        bounded = true
    )

    CompositionLocalProvider(LocalIndication provides customRipple) {
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
            elevation = ButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = if (enabled) backgroundColor else Color.DarkGray,
                contentColor = Color.Unspecified
            ),
            border = BorderStroke(1.5.dp, MainTestTheme.colors.primaryBackground.copy(alpha = 0.4f))
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