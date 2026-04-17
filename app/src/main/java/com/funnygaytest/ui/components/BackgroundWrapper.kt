package com.funnygaytest.ui.components

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import timber.log.Timber

@SuppressLint("LocalContextResourcesRead")
@Composable
fun BackgroundWrapper(
    modifier: Modifier = Modifier,
    @DrawableRes backgroundId: Int,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current

    val safeId = remember(backgroundId) {
        try {
            context.resources.getResourceName(backgroundId)
            backgroundId
        } catch (_: Resources.NotFoundException) {
            Timber.e("ERROR: Resource $backgroundId not found!")
            0
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Crossfade(
            targetState = safeId,
            animationSpec = tween(durationMillis = 1000),
            label = "BackgroundTransition"
        ) { targetId ->
            if (targetId != 0) {
                Image(
                    painter = painterResource(id = targetId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            } else {
                Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)))
            }
        }
        Box(modifier = modifier.fillMaxSize()) {
            content()
        }
    }
}