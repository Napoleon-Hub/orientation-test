package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.R
import com.funnygaytest.platform.locale.AppLanguage
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun LanguageToggleButton(
    modifier: Modifier = Modifier,
    currentLanguage: AppLanguage,
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
        Image(
            painter = painterResource(id = currentLanguage.flagRes),
            contentDescription = stringResource(R.string.start_select_language_button),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )
    }
}
