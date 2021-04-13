package com.kakeibo.ui.listener

import android.view.View
import com.kakeibo.data.Category

interface CategoryClickListener {
    fun onCategoryClicked(view: View, category: Category)
}