package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.funnygaytest.R
import com.funnygaytest.ui.themes.MainTestTheme

@Composable
fun LanguageToggleButton(
    currentLanguage: String,
    onClick: () -> Unit
) {
    val flagRes = if (currentLanguage.startsWith("ru")) {
        R.drawable.ic_flag_ru
    } else if(currentLanguage.startsWith("en")) {
        R.drawable.ic_flag_en
    } else R.drawable.ic_flag_de

    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(12.dp)
            .size(48.dp)
            .background(MainTestTheme.colors.primaryElement.copy(alpha = 0.4f), CircleShape)
            .border(1.dp, MainTestTheme.colors.primaryBackground.copy(alpha = 0.5f), CircleShape)
    ) {
        Image(
            painter = painterResource(id = flagRes),
            contentDescription = stringResource(R.string.start_select_language_button),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )
    }
}