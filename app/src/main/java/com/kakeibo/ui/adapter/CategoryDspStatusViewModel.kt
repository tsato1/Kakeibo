package com.kakeibo.ui.adapter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryDspStatus
import com.kakeibo.data.DataRepository

class CategoryDspStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<CategoryDspStatus>> = repository.categoryDspStatuses

    fun insert(categoryDspStatuses: List<CategoryDspStatus>) {
        repository.insertCategoryDsps(categoryDspStatuses)
    }
}