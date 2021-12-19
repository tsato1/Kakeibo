package com.kakeibo.feature_settings.domain.use_cases

import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.GetCustomCategoryByIdUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_detail.InsertCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.DeleteCustomCategoryUseCase
import com.kakeibo.feature_settings.domain.use_cases.custom_category_list.GetAllCustomCategoriesUseCase

data class CustomCategoryUseCases(
    val getAllCustomCategoriesUseCase: GetAllCustomCategoriesUseCase,
    val getCustomCategoryByIdUseCase: GetCustomCategoryByIdUseCase,
    val insertCustomCategoryUseCase: InsertCustomCategoryUseCase,
    val deleteCategoryUseCase: DeleteCustomCategoryUseCase
)