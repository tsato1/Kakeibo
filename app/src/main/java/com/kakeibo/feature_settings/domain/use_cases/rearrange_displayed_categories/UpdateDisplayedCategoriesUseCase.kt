package com.kakeibo.feature_settings.domain.use_cases.rearrange_displayed_categories

import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import com.kakeibo.util.UtilCategory

class UpdateDisplayedCategoriesUseCase(
    private val repository: CategoryRearrangeRepository
) {

    @Throws(CategoryDspEntity.InvalidCategoryDspException::class)
    suspend operator fun invoke(list: List<CategoryModel>) {

        if (list.size > UtilCategory.NUM_MAX_DSP_CATEGORIES) {
            throw CategoryDspEntity.InvalidCategoryDspException(
                "You have exceeded the maximum number of Categories. Max = 16."
            )
        }

        val categoryDsps = mutableListOf<CategoryDspEntity>()
        list.forEachIndexed { index, categoryModel ->
            categoryDsps.add(
                CategoryDspEntity(
                    _id = categoryModel._id,
                    code = categoryModel.code,
                    location = index
                )
            )
        }
        return repository.updateDisplayedCategories(categoryDsps)
    }
}