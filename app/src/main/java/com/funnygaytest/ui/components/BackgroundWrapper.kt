package com.funnygaytest.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun BackgroundWrapper(
    modifier: Modifier = Modifier,
    @DrawableRes backgroundId: Int,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = backgroundId,
            animationSpec = tween(durationMillis = 1000),
            label = "BackgroundTransition"
        ) { targetBackgroundId ->
            Image(
                painter = painterResource(id = targetBackgroundId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center
            )
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        ) {
            content()
        }
    }
}