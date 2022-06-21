package com.funnygaytest.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MainTestTheme.colors.primaryElement,
    iconId: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableStateOf(0L) }

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
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = backgroundColor)
    ) {
        Icon(painter = painterResource(id = iconId), contentDescription = null, tint = Color.White)
    }
}