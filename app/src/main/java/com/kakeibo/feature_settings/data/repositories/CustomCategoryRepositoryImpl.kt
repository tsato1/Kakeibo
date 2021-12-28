package com.kakeibo.feature_settings.data.repositories

import com.kakeibo.core.data.local.CategoryDao
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.repositories.CustomCategoryRepository
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import java.io.IOException

class CustomCategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CustomCategoryRepository {

    override suspend fun getCustomCategoryById(id: Long): CategoryModel {
        return categoryDao.getCustomCategoryById(id).toCategoryModel()
    }

    override fun getAllCustomCategories(): Flow<Resource<List<CategoryModel>>> = flow {
        emit(Resource.Loading())

        val customCategories = categoryDao
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

        val flow = categoryDao
            .getAllCustomCategories()
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

    override suspend fun insertCustomCategory(categoryModel: CategoryModel): Long {
        return categoryDao.insertCategory(categoryModel.toCategoryEntity())
    }

    override suspend fun deleteCustomCategory(categoryModel: CategoryModel) {
        categoryDao.deleteCategoryById(categoryModel._id)
    }

}