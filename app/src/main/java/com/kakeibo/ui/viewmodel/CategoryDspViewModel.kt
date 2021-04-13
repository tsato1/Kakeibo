package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryDsp
import com.kakeibo.data.DataRepository

class CategoryDspViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<CategoryDsp>> = repository.categoryDsps

    fun insertAll(categoryDsps: List<CategoryDsp>) {
        repository.insertCategoryDsps(categoryDsps)
    }
}