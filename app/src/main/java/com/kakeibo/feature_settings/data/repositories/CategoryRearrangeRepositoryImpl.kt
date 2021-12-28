package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.data.local.CategoryDao
import com.kakeibo.core.data.local.CategoryDspDao
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CategoryRearrangeRepository
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class CategoryRearrangeRepositoryImpl(
    private val categoryDao: CategoryDao,
    private val categoryDspDao: CategoryDspDao
) : CategoryRearrangeRepository {

    override fun getDisplayedCategories(): Flow<Resource<List<CategoryModel>>> = flow {
        emit(Resource.Loading())

        val displayedCategories = categoryDao
            .getAllDisplayedCategories()
            .map {
                it.map {
                    it.toCategoryModel()
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

        val flow = categoryDao
            .getAllDisplayedCategories()
            .map {
                it.map {
                    it.toCategoryModel()
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

    override fun getNonDisplayedCategories(): Flow<Resource<List<CategoryModel>>> = flow {
        emit(Resource.Loading())

        val nonDisplayedCategories = categoryDao
            .getAllNotDisplayedCategories()
            .map {
                it.map {
                    it.toCategoryModel()
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

        val flow = categoryDao
            .getAllNotDisplayedCategories()
            .map {
                it.map {
                    it.toCategoryModel()
                }
            }
            .map {
                Resource.Success(it)
            }

        emitAll(flow)
    }

    override suspend fun updateDisplayedCategories(list: List<CategoryDspEntity>) {
        categoryDspDao.deleteAllCategoryDsps()
        categoryDspDao.insertCategoryDsps(list)
    }

}