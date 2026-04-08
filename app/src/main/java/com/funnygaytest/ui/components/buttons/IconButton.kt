package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedButton
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableLongStateOf
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
    rippleColor: Color = MainTestTheme.colors.primaryElement,
    iconId: Int,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }

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
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                backgroundColor = if (enabled) backgroundColor else Color.DarkGray,
                contentColor = Color.Unspecified
            ),
            border = BorderStroke(1.5.dp, MainTestTheme.colors.primaryBackground.copy(alpha = 0.4f))
        ) {
            Icon(painter = painterResource(id = iconId), contentDescription = null, tint = Color.White)
        }
    }
}