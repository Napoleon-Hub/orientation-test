package com.funnygaytest.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.verticalFadingEdges(scrollState: ScrollState, fadeHeight: Dp): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val fadePx = fadeHeight.toPx().coerceAtMost(size.height / 2)
        if (scrollState.canScrollBackward) {
            drawRect(
                brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black), endY = fadePx),
                size = Size(size.width, fadePx),
                blendMode = BlendMode.DstIn
            )
        }
        if (scrollState.canScrollForward) {
            val top = size.height - fadePx
            drawRect(
                brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent), startY = top, endY = size.height),
                topLeft = Offset(0f, top),
                size = Size(size.width, fadePx),
                blendMode = BlendMode.DstIn
            )
        }
    }

fun Modifier.verticalScrollIndicator(
    scrollState: ScrollState,
    color: Color,
    width: Dp = 4.dp
): Modifier = drawWithContent {
    drawContent()
    val maxScroll = scrollState.maxValue
    if (maxScroll == 0 || maxScroll == Int.MAX_VALUE) return@drawWithContent

    val viewport = size.height
    val thumbHeight = viewport * viewport / (viewport + maxScroll)
    val thumbTop = (viewport - thumbHeight) * scrollState.value / maxScroll
    val widthPx = width.toPx()
    val left = size.width - widthPx
    val radius = CornerRadius(widthPx / 2)

    drawRoundRect(
        color = color.copy(alpha = 0.15f),
        topLeft = Offset(left, 0f),
        size = Size(widthPx, viewport),
        cornerRadius = radius
    )
    drawRoundRect(
        color = color,
        topLeft = Offset(left, thumbTop),
        size = Size(widthPx, thumbHeight),
        cornerRadius = radius
    )
}
