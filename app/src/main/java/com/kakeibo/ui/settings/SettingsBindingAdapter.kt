package com.kakeibo.ui.settings

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.kakeibo.R
import com.kakeibo.ui.settings.category.edit.CustomCategoryViewModel
import com.kakeibo.util.UtilCategory

/*
 * used in fragment_settings_custom_category_color.xml
 */
@BindingAdapter("bind:customCategory")
fun setColorButton(view: View, customCategoryViewModel: CustomCategoryViewModel?) {

    val incomeCheckImageView = view.findViewById<ImageView>(R.id.imv_category_add_in)
    val expenseCheckImageView = view.findViewById<ImageView>(R.id.imv_category_add_ex)

    customCategoryViewModel?.let {
        when (it.color.value) {
            UtilCategory.CATEGORY_COLOR_INCOME -> {
                incomeCheckImageView.visibility = View.VISIBLE
                expenseCheckImageView.visibility = View.INVISIBLE
            }
            UtilCategory.CATEGORY_COLOR_EXPENSE -> {
                incomeCheckImageView.visibility = View.INVISIBLE
                expenseCheckImageView.visibility = View.VISIBLE
            }
            -1 -> {
                incomeCheckImageView.visibility = View.INVISIBLE
                expenseCheckImageView.visibility = View.INVISIBLE
            }
        }
    }
}