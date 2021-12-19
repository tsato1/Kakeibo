package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.feature_main.domain.models.DisplayedCategory

interface DisplayedCategoryRepository {

    suspend fun observeDisplayedCategories(): List<DisplayedCategory>

}