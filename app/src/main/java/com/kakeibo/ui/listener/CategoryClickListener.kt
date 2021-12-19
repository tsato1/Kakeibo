package com.kakeibo.ui.listener

import android.view.View
import com.kakeibo.core.data.local.entities.CategoryEntity

interface CategoryClickListener {
    fun onCategoryClicked(view: View, categoryEntity: CategoryEntity)
}