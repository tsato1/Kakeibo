package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.data.sources.local.CustomCategoryDao
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class CustomCategoryRepositoryImpl(
    private val customCategoryDao: CustomCategoryDao
) : CustomCategoryRepository {

    override suspend fun getCustomCategoryById(id: Long): CategoryModel {
        return customCategoryDao.getCustomCategoryById(id).toCategoryModel()
    }

    override fun getAllCustomCategories(): Flow<Resource<List<CategoryModel>>> = flow {
        emit(Resource.Loading())

        val customCategories = customCategoryDao
            .getAllCustomCategories()
            .map {
                it.map {
                    it.toCategoryModel()
                }
            }
            .first()

        emit(Resource.Loading(customCategories))

        try {
//            val remoteData = api.getCustomCategories()
            //insert data into database
        } catch (e: HttpException) {
            emit(Resource.Error(e.message ?: "HttpException", data = customCategories))
        } catch (e: IOException) {
            emit(Resource.Error(e.message ?: "Couldn't reach server", data = customCategories))
        }

        val flow = customCategoryDao
            .getAllCustomCategories()
            .map {
                it.map {
                    it.toCategoryModel()
                }
            }
            .map { Resource.Success(it) }

        emitAll(flow)
    }

    override suspend fun insertCustomCategory(categoryModel: CategoryModel): Long {
        return customCategoryDao.insertCustomCategory(categoryModel.toCategoryEntity())
    }

    override suspend fun deleteCustomCategory(categoryModel: CategoryModel) {
        customCategoryDao.deleteCustomCategory(categoryModel.toCategoryEntity())
    }

}