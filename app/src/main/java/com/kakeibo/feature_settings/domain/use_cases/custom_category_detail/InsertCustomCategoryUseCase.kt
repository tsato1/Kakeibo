package com.kakeibo.feature_settings.domain.use_cases.custom_category_detail

import android.content.Context
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
            throw CategoryModel.InvalidCustomCategoryException(
                "Category Type is not selected. Select one."
            )
        }
        if (categoryModel.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
            throw CategoryModel.InvalidCustomCategoryException(
                "Something is wrong with the custom category. Please report this to the developer."
            )
        }
        if (categoryModel.code >= UtilCategory.CUSTOM_CATEGORY_CODE_START + UtilCategory.NUM_MAX_CUSTOM_CATEGORY) { // todo
            throw CategoryModel.InvalidCustomCategoryException(
                "The number of custom categories has reached the maximum number allowed. Delete first."//todo
//                context.getString(R.string.)
            )
        }
        if (categoryModel.name.isBlank()) {
            throw CategoryModel.InvalidCustomCategoryException(
                "Custom Category Name cannot be empty."
//                context.getString(R.string.err_category_name_cannot_be_empty)
            )
        }

        repository.insertCustomCategory(categoryModel)
    }

}