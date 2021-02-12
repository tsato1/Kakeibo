package com.kakeibo

object Constants {
    val CATEGORY_COLORS = arrayOf(
            "#2b381d", "#40552b", "#557238", "#80aa55", "#aac78d",
            "#eaf1e2", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8"
    )

    // Use the fake local server data or real remote server.
    const val USE_FAKE_SERVER = false
    const val BASIC_SKU = "plus_1y"
    const val PREMIUM_SKU = "plus_1m"
    const val PLAY_STORE_SUBSCRIPTION_URL = "https://play.google.com/store/account/subscriptions"
    const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"
}