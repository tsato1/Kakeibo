package com.kakeibo.feature_settings.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CustomCategoryRepository {

    suspend fun getCustomCategoryById(id: Long): CategoryModel?

    fun getAllCustomCategories(): Flow<Resource<List<CategoryModel>>>

    suspend fun insertCustomCategory(categoryModel: CategoryModel): Long

    suspend fun deleteCustomCategory(categoryModel: CategoryModel)

}