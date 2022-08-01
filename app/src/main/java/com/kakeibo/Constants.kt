package com.kakeibo

object Constants {
    val IGNORE_AUTH_URLS = listOf("/login", "/register")

    const val BASE_URL = "http://10.0.2.2:8080"

    val CATEGORY_EXPENSE_COLORS = arrayOf(
        "#827717", "#9E9D24", "#AFB42B", "#C0CA33", "#CDDC39",
        "#D4E157", "#DCE775", "#E6EE9C", "#F0F4C3", "#F9FBE7"
    )
    val CATEGORY_INCOME_COLORS = arrayOf(
        "#E65100", "#EF6C00", "#F57C00", "#FB8C00", "#FF9800",
        "#FFA726", "#FFB74D", "#FFCC80", "#FFE0B2", "#FFF3E0"
    )

    const val USE_FAKE_SERVER = false
    const val BASIC_SKU = "plus_1m"
    const val PREMIUM_SKU = "plus_1y"
    const val PLAY_STORE_SUBSCRIPTION_URL = "https://play.google.com/store/account/subscriptions"
    const val PLAY_STORE_SUBSCRIPTION_DEEPLINK_URL = "https://play.google.com/store/account/subscriptions?sku=%s&package=%s"

    /* Shared Preferences */
    const val SHARED_PREF_NAME = "com.kakeibo_preferences"
    const val ENCRYPTED_SHARED_PREF_NAME = "com.kakeibo_enc_preferences"
    const val KEY_LOGGED_IN_EMAIL = "KEY_LOGGED_IN_EMAIL"
    const val KEY_PASSWORD = "KEY_PASSWORD"
    const val NO_EMAIL = "NO_EMAIL"
    const val NO_PASSWORD = "NO_PASSWORD"
}