package com.funnygaytest.ui.themes.components

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.themes.GayTestTheme

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = GayTestTheme.colors.primaryElement,
    onClick: () -> Unit,
    text: String? = null,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableStateOf(0L) }

    OutlinedButton(
        modifier = modifier.height(66.dp),
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
                style = GayTestTheme.typography.buttonText,
                color = GayTestTheme.colors.primaryText
            )
        }
    }
}