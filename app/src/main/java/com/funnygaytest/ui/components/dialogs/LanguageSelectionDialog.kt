package com.funnygaytest.ui.components.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.funnygaytest.R
import com.funnygaytest.ui.theme.LabTheme

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = LabTheme.colors.surface,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_select_language_button),
                    style = LabTheme.typography.heading,
                    color = LabTheme.colors.textPrimary
                )

                LanguageOption(
                    text = "Русский",
                    iconRes = R.drawable.ic_flag_ru,
                    isSelected = currentLanguage.startsWith("ru"),
                    onClick = { onLanguageSelected("ru") }
                )

                LanguageOption(
                    text = "English",
                    iconRes = R.drawable.ic_flag_en,
                    isSelected = currentLanguage.startsWith("en"),
                    onClick = { onLanguageSelected("en") }
                )

                LanguageOption(
                    text = "Deutsch",
                    iconRes = R.drawable.ic_flag_de,
                    isSelected = currentLanguage.startsWith("de"),
                    onClick = { onLanguageSelected("de") }
                )
            }
        }
    }
}

@Composable
fun LanguageOption(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        LabTheme.colors.accent.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .border(1.dp, LabTheme.colors.textPrimary.copy(alpha = 0.3f), CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = LabTheme.typography.body,
            color = LabTheme.colors.textPrimary
        )
    }
}