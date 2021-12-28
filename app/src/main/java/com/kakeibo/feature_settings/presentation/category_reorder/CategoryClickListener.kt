package com.kakeibo.feature_settings.presentation.category_reorder

import android.view.View
import com.kakeibo.core.data.local.entities.CategoryEntity

interface CategoryClickListener {
    fun onCategoryClicked(view: View, categoryEntity: CategoryEntity)
}