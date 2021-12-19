package com.kakeibo.feature_settings.domain.util

sealed class CustomCategoryListOrder() {
    object Name: CustomCategoryListOrder()
    object Code: CustomCategoryListOrder()
    object Color: CustomCategoryListOrder()
}