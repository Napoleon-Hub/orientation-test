package com.funnygaytest.ui.components.buttons

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Suppress("ModifierFactoryExtensionFunction")
fun ColumnScope.adaptiveButtonModifier(maxHeight: Dp): Modifier = Modifier
    .fillMaxWidth()
    .weight(1f, fill = false)
    .heightIn(max = maxHeight)
    .fillMaxHeight()
