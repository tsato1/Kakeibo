package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kakeibo.SubApp
import com.kakeibo.data.CategoryStatus
import com.kakeibo.data.DataRepository

class CategoryStatusViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<CategoryStatus>> = repository.categories
    val allDsp: LiveData<List<CategoryStatus>> = repository.categoriesForDisplay
    val allMap: LiveData<Map<Int, CategoryStatus>> = Transformations.map(all) { all ->
        all.associateBy( { it.code }, { it })
    }
    val allCodes: LiveData<List<Int>> = Transformations.map(all) { all ->
        all.map{ it.code }
    }

    fun insert(categoryStatus: CategoryStatus) {
        repository.insertCategory(categoryStatus)
    }
}