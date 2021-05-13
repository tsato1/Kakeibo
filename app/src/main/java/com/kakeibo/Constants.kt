package com.kakeibo

object Constants {
    val CATEGORY_EXPENSE_COLORS = arrayOf(
            "#2b381d", "#40552b", "#557238", "#80aa55", "#aac78d",
            "#eaf1e2", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8", "#fafcf8",
            "#fafcf8"
    )
    val CATEGORY_INCOME_COLORS = arrayOf(
            "#E64A19", "#FF5722", "#FD9149", "#FDA061", "#FDAE79",
            "#FDBD91", "#feccaa", "#fedac2", "#fee9da", "#fef7f2"
    )

    const val USE_FAKE_SERVER = false
    const val BASIC_SKU = "plus_1m"
    const val PREMIUM_SKU = "plus_1y"
    const val PLAY_STORE_SUBSCRIPTION_URL = "https://play.google.com/store/account/subscriptions"
    const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"
}