package com.funnygaytest.ui.themes.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.funnygaytest.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdvertView(modifier: Modifier = Modifier, width: Int) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
                    context,
                    width
                )
                adUnitId = context.getString(R.string.banner_ad_unit_id)
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        },
        modifier = modifier
    )
}