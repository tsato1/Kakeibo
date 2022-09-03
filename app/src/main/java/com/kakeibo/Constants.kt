package com.kakeibo

object Constants {
    val CATEGORY_EXPENSE_COLORS = arrayOf(
        "#827717", "#9E9D24", "#AFB42B", "#C0CA33", "#CDDC39",
        "#D4E157", "#DCE775", "#E6EE9C", "#F0F4C3", "#F9FBE7"
    )
    val CATEGORY_INCOME_COLORS = arrayOf(
        "#E65100", "#EF6C00", "#F57C00", "#FB8C00", "#FF9800",
        "#FFA726", "#FFB74D", "#FFCC80", "#FFE0B2", "#FFF3E0"
    )

    /* Subscription */
    const val USE_FAKE_SERVER = false
    const val BASIC_SKU = "plus_1m"
    const val PREMIUM_SKU = "plus_1y"
    const val PLAY_STORE_SUBSCRIPTION_URL = "https://play.google.com/store/account/subscriptions"
    const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"

    /* Shared Preferences */
    const val SHARED_PREF_NAME = "com.kakeibo_preferences"
    const val ENCRYPTED_SHARED_PREF_NAME = "com.kakeibo_enc_preferences"
    const val PREFS_KEY_JWT_ACCESS_TOKEN = "jwt_access"
    const val PREFS_KEY_JWT_REFRESH_TOKEN = "jwt_refresh"
    const val NO_JWT_TOKEN = "NO_JWT_TOKEN"

    /* Remote */
    const val IGNORE_AUTH_URLS = ""
    const val ITEM_BASE_URL = "http://10.0.2.2:8080"
    const val AUTH_BASE_URL = "http://10.0.2.2:8081"
}