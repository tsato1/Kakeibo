package com.kakeibo.feature_settings.presentation.custom_category_detail

import android.graphics.Bitmap
import androidx.compose.ui.focus.FocusState

sealed class CustomCategoryDetailEvent {
    data class TypeChanged(val type: Int) : CustomCategoryDetailEvent()
    data class NameEntered(val name: String) : CustomCategoryDetailEvent()
    data class NameFocusChanged(val focusState: FocusState) : CustomCategoryDetailEvent()
    data class Save(val bitmap: Bitmap) : CustomCategoryDetailEvent()
}