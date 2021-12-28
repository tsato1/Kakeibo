package com.kakeibo.feature_main.data.repositories

import com.kakeibo.core.data.local.CategoryDao
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.repositories.DisplayedCategoryRepository
import com.kakeibo.feature_main.domain.models.DisplayedCategoryModel
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class DisplayedCategoryRepositoryImpl(
    private val dao: CategoryDao
) : DisplayedCategoryRepository {

    override fun getAllDisplayedCategories(): Flow<Resource<List<DisplayedCategoryModel>>> = flow {
        emit(Resource.Loading())

        val displayedCategories = dao
            .getAllDisplayedCategories()
            .map {
                it.map {
                    it.toDisplayedCategoryModel()
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
            .map {
                it.map {
                    it.toDisplayedCategoryModel()
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

}