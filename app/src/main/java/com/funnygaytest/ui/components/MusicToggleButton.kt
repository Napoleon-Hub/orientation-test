package com.funnygaytest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun MusicToggleButton(
    modifier: Modifier = Modifier,
    isMuted: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(16.dp)
            .size(48.dp)
            .background(MainTestTheme.colors.primaryElement.copy(alpha = 0.4f), CircleShape)
            .border(1.dp, MainTestTheme.colors.primaryBackground.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Music Toggle",
            tint = MainTestTheme.colors.primaryText
        )
    }
}