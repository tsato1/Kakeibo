package com.kakeibo.ui.adapter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.DataRepository

class CategoryStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<CategoryStatus>> = repository.categories
    val allCodes: LiveData<List<Int>> = repository.categoryCodes
    val categoriesForDisplay: LiveData<List<CategoryStatus>> = repository.categoriesForDisplay

    fun insert(categoryStatus: CategoryStatus) {
        repository.insertCategory(categoryStatus)
    }
}