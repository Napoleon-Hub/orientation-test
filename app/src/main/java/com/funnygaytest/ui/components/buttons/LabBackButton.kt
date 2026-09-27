package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.R
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun LabBackButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val size = LabTheme.dimens.buttonHeight
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(LabTheme.colors.accent.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = LabTheme.colors.textSecondary,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.back_button_description),
            tint = LabTheme.colors.textPrimary.copy(alpha = 0.8f),
            modifier = Modifier.size(size * 0.42f)
        )
    }
}