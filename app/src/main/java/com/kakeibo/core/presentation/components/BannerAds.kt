//package com.kakeibo.core.presentation.components
//
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.viewinterop.AndroidView
//import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.AdSize
//import com.google.android.gms.ads.AdView
//import com.kakeibo.BuildConfig
//
//@Composable
//fun BannerAds(
//    modifier: Modifier = Modifier,
//    adId: String
//) {
//    AndroidView(
//        modifier = modifier.fillMaxWidth(),
//        factory = { context ->
//            AdView(context).apply {
//                setAdSize(AdSize.BANNER)
//                if (BuildConfig.DEBUG) {
//                    adUnitId = "ca-app-pub-3940256099942544/6300978111"
//                }
//                else {
//                    adUnitId = adId
//                }
//                loadAd(AdRequest.Builder().build())
//            }
//        }
//    )
//}