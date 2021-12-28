package com.kakeibo.feature_main.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import kotlinx.coroutines.flow.Flow

interface DisplayedCategoryRepository {

    fun getAllDisplayedCategories(): Flow<Resource<List<DisplayedCategoryModel>>>

}