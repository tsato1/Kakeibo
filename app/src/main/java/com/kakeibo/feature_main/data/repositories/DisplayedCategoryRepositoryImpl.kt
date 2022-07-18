package com.kakeibo.feature_main.data.repositories

import android.content.Context
import com.kakeibo.R
import com.kakeibo.core.data.local.CategoryDao
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import com.kakeibo.util.UtilCategory
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class DisplayedCategoryRepositoryImpl(
    context: Context,
    private val dao: CategoryDao
) : DisplayedCategoryRepository {

    private val defaultCategories = context.resources.getStringArray(R.array.default_category)

    override fun getAllDisplayedCategories(): Flow<Resource<List<DisplayedCategoryModel>>> = flow {
        emit(Resource.Loading())

        val displayedCategories = dao
            .getAllDisplayedCategories()
            .map { list ->
                list.map { categoryEntity ->
                    categoryEntity.toDisplayedCategoryModel().also { displayedCategoryModel ->
                        if (displayedCategoryModel.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                            displayedCategoryModel.name =
                                defaultCategories[displayedCategoryModel.code]
                        }
                    }
                }
            }
            .first()

        emit(Resource.Loading(displayedCategories))

        try {

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "HttpException", data = displayedCategories))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Couldn't reach server", data = displayedCategories))
        }

        val flow = dao
            .getAllDisplayedCategories()
            .map { list ->
                list.map { categoryEntity ->
                    categoryEntity.toDisplayedCategoryModel().also { displayedCategoryModel ->
                        if (displayedCategoryModel.code < UtilCategory.CUSTOM_CATEGORY_CODE_START) {
                            displayedCategoryModel.name =
                                defaultCategories[displayedCategoryModel.code]
                        }
                    }
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

}