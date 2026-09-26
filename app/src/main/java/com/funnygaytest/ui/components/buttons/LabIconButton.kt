package com.funnygaytest.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.theme.LabTheme

private const val IconHeightFraction = 0.55f

@Composable
fun LabIconButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = LabTheme.colors.surface,
    rippleColor: Color = LabTheme.colors.accent,
    @DrawableRes iconId: Int,
    contentDescription: String?,
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
            contentPadding = PaddingValues(0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = backgroundColor,
                contentColor = Color.Unspecified,
                disabledContainerColor = LabTheme.colors.disabled,
                disabledContentColor = Color.Unspecified
            ),
            border = BorderStroke(1.5.dp, LabTheme.colors.accent.copy(alpha = 0.4f))
        ) {
            Icon(
                modifier = Modifier
                    .fillMaxHeight(IconHeightFraction)
                    .aspectRatio(1f),
                painter = painterResource(id = iconId),
                contentDescription = contentDescription,
                tint = LabTheme.colors.textEmphasis
            )
        }
    }
}
