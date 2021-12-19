package com.kakeibo.feature_settings.presentation.util

sealed class Screen(val route: String, val label: String?) {
    object SettingsListScreen: Screen("settings_list", "Settings")
    object CustomCategoryListScreen: Screen("custom_category_list", "Custom Categories")
    object CustomCategoryDetailScreen: Screen("custom_category_detail", "Custom Category Detail")
    object RearrangeCategoriesScreen: Screen("rearrange_categories", "Rearrange Categories")
    object RearrangeCategoriesSubtractScreen: Screen("rearrange_categories_subtract", "Rearrange Categories Subtract")
    object RearrangeCategoriesAddScreen: Screen("reaarange_categories_add", "Rearrange Categories Add")
    object AboutScreen: Screen("about", "About")
}