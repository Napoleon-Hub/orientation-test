package com.funnygaytest.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MainTestTheme.colors.primaryElement,
    text: String? = null,
    textStyle: TextStyle = MainTestTheme.typography.buttonText,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableStateOf(0L) }

    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }


    OutlinedButton(
        modifier = modifier,
        onClick = {
            val time = System.currentTimeMillis()
            if (time - lastClickTime.value >= 500L) {
                lastClickTime.value = time
                onClick()
            }
        },
        enabled = enabled,
        elevation = ButtonDefaults.elevation(
            defaultElevation = 3.dp,
            pressedElevation = 5.dp,
            disabledElevation = 0.dp
        ),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = backgroundColor)
    ) {

        text?.let {
            Text(
                text = it,
                modifier = modifier.drawWithContent {
                    if (readyToDraw) {
                        drawContent()
                    }
                },
                style = scaledTextStyle,
                softWrap = false,
                onTextLayout = { textLayoutResult ->
                    if (textLayoutResult.didOverflowWidth) {
                        scaledTextStyle =
                            scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
                    } else {
                        readyToDraw = true
                    }
                }
            )
        }
    }
}