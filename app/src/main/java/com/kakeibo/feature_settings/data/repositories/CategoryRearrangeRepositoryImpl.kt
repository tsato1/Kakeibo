package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.data.local.CategoryDspDao
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedCategory
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class CategoryRearrangeRepositoryImpl(
    private val dao: CategoryDspDao
) : CategoryRearrangeRepository {

    override fun getDisplayedCategories(): Flow<Resource<List<DisplayedCategory>>> = flow {
        emit(Resource.Loading())

        val displayedCategories = dao
            .getDisplayedCategoryList()
            .map {
                it.map {
                    it.toDisplayedCategory()
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
            .getDisplayedCategoryList()
            .map {
                it.map {
                    it.toDisplayedCategory()
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

    override fun getNonDisplayedCategories(): Flow<Resource<List<DisplayedCategory>>> = flow {
        emit(Resource.Loading())

        val nonDisplayedCategories = dao
            .getNonDisplayedCategoryList()
            .map {
                it.map {
                    it.toDisplayedCategory()
                }
            }
            .first()

        emit(Resource.Loading(nonDisplayedCategories))

        try {

        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "HttpException", data = nonDisplayedCategories))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Couldn't reach server", data = nonDisplayedCategories))
        }

        val flow = dao
            .getNonDisplayedCategoryList()
            .map {
                it.map {
                    it.toDisplayedCategory()
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

    override suspend fun updateDisplayedCategories(list: List<DisplayedCategory>) {

    }

    override suspend fun updateNonDisplayedCategories(list: List<DisplayedCategory>) {

    }


}