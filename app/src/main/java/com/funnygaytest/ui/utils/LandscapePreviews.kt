package com.funnygaytest.ui.utils

import androidx.compose.ui.tooling.preview.Preview

/**
 * Превью экрана на крайних размерах: приложение работает только в ландшафте,
 * поэтому проверяем самый низкий телефон, обычный телефон и планшеты 16:10 и 4:3.
 */
@Preview(name = "Small phone 640x320", device = "spec:width=640dp,height=320dp,dpi=320")
@Preview(name = "Phone 891x411", device = "spec:width=891dp,height=411dp,dpi=420")
@Preview(name = "Tablet 1280x800", device = "spec:width=1280dp,height=800dp,dpi=240")
@Preview(name = "Tablet 4:3 1024x768", device = "spec:width=1024dp,height=768dp,dpi=320")
annotation class LandscapePreviews
