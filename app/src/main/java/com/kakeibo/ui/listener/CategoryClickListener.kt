package com.kakeibo.ui.listener

import android.view.View
import com.kakeibo.data.CategoryStatus

interface CategoryClickListener {
    fun onCategoryClicked(view: View, category: CategoryStatus)
}