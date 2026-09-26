package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.components.shrinkToFit
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LabTheme.colors.surface,
    rippleColor: Color = LabTheme.colors.accent,
    text: String? = null,
    textStyle: TextStyle = LabTheme.typography.button,
    autoSize: TextAutoSize = shrinkToFit(textStyle),
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(color = rippleColor)) {
        OutlinedButton(
            modifier = modifier,
            onClick = onClick,
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.dp,
                pressedElevation = 8.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = Color.Unspecified,
                disabledContainerColor = LabTheme.colors.disabled,
                disabledContentColor = Color.Unspecified
            ),
            border = BorderStroke(1.5.dp, LabTheme.colors.accent.copy(alpha = 0.4f))
        ) {

            text?.let {
                BasicText(
                    text = it,
                    style = textStyle,
                    softWrap = false,
                    maxLines = 1,
                    autoSize = autoSize
                )
            }
        }
    }
}