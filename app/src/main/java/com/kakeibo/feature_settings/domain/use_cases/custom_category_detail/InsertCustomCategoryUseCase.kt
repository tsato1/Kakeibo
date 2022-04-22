package com.kakeibo.feature_settings.domain.use_cases.custom_category_detail

import android.content.Context
import com.kakeibo.R
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import com.kakeibo.util.UtilCategory

class InsertCustomCategoryUseCase(
    private val repository: CustomCategoryRepository,
    private val context: Context
) {

    @Throws(CategoryModel.InvalidCustomCategoryException::class)
    suspend operator fun invoke(categoryModel: CategoryModel) { // return long
        if (categoryModel.color != UtilCategory.CATEGORY_COLOR_EXPENSE &&
            categoryModel.color != UtilCategory.CATEGORY_COLOR_INCOME) {
                val str = context.getString(R.string.err_category_type_not_selected)
                throw CategoryModel.InvalidCustomCategoryException(str)
        }
        if (categoryModel.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            val str = context.getString(R.string.error)
            throw CategoryModel.InvalidCustomCategoryException(str)
        }
        if (categoryModel.code >= UtilCategory.CUSTOM_CATEGORY_CODE_START + UtilCategory.NUM_MAX_CUSTOM_CATEGORY) {
            val str = context.getString(R.string.err_reached_max_count)
            throw CategoryModel.InvalidCustomCategoryException(str)
        }
        if (categoryModel.name.isBlank()) {
            val str = context.getString(R.string.err_category_name_cannot_be_empty)
            throw CategoryModel.InvalidCustomCategoryException(str)
        }

        repository.insertCustomCategory(categoryModel)
    }

}