package com.funnygaytest.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.funnygaytest.R
import com.funnygaytest.ui.theme.LabTheme
import com.funnygaytest.ui.theme.scaled

@Composable
fun EndingCard(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconRes: Int,
    finalTextShown: Boolean = false
) {
    val dimens = LabTheme.dimens
    val borderColor = LabTheme.colors.accent
    val bgColor = LabTheme.colors.accent.copy(alpha = 0.25f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = bgColor, shape = RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimens.endingIconSize)
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.matchParentSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.result_endings_new),
                style = LabTheme.typography.caption.scaled(dimens.textScale),
                color = LabTheme.colors.textPrimary
            )

            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = title,
                style = LabTheme.typography.heading.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    .scaled(dimens.textScale),
                color = LabTheme.colors.textPrimary,
                softWrap = true
            )

            if (finalTextShown) {
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = stringResource(R.string.result_endings_all_unlocked),
                    style = LabTheme.typography.caption.copy(fontSize = 10.sp).scaled(dimens.textScale),
                    color = LabTheme.colors.textPrimary,
                    textAlign = TextAlign.Start,
                    softWrap = true
                )
            }
        }
    }
}