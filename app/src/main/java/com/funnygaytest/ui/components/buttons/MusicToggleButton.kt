package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.R
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun MusicToggleButton(
    modifier: Modifier = Modifier,
    isMuted: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(12.dp)
            .size(48.dp)
            .background(LabTheme.colors.surface.copy(alpha = 0.4f), CircleShape)
            .border(1.dp, LabTheme.colors.accent.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            painter = painterResource(if (isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_up),
            contentDescription = stringResource(R.string.music_toggle_button_description),
            tint = LabTheme.colors.textPrimary
        )
    }
}