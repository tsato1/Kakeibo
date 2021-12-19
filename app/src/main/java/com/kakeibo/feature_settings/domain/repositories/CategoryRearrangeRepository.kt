package com.kakeibo.feature_settings.domain.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedCategory
import kotlinx.coroutines.flow.Flow

interface CategoryRearrangeRepository {

    fun getDisplayedCategories(): Flow<Resource<List<DisplayedCategory>>>

    fun getNonDisplayedCategories(): Flow<Resource<List<DisplayedCategory>>>

    suspend fun updateDisplayedCategories(list: List<DisplayedCategory>)

    suspend fun updateNonDisplayedCategories(list: List<DisplayedCategory>)

}