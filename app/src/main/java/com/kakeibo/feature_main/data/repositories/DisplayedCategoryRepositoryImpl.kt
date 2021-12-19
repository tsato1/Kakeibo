package com.kakeibo.feature_main.data.repositories

import com.kakeibo.core.data.local.CategoryDspDao
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.models.DisplayedCategory

class DisplayedCategoryRepositoryImpl(
    private val categoryDspDao: CategoryDspDao
) : DisplayedCategoryRepository {

    override suspend fun observeDisplayedCategories(): List<DisplayedCategory> {
        return categoryDspDao.observeDisplayedCategoryList().map { it.toDisplayedCategory() }
    }

}