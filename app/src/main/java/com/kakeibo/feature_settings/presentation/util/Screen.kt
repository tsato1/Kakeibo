package com.kakeibo.feature_settings.presentation.util

sealed class Screen(val route: String, val label: String?) {
    object SettingsListScreen: Screen("settings_list", "Settings")
    object CustomCategoryListScreen: Screen("custom_category_list", "Custom Categories")
    object CustomCategoryDetailScreen: Screen("custom_category_detail", "Custom Category Detail")
    object CategoryRearrangeScreen: Screen("category_rearrange", "Category Rearrange")
}