package com.kakeibo.feature_settings.domain.repositories

import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryRearrangeRepository {

    fun getDisplayedCategories(): Flow<Resource<List<CategoryModel>>>

    fun getNonDisplayedCategories(): Flow<Resource<List<CategoryModel>>>

    suspend fun updateDisplayedCategories(list: List<CategoryDspEntity>)

}