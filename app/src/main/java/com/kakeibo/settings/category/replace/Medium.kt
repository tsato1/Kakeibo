package com.kakeibo.settings.category.replace

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakeibo.data.CategoryStatus

class Medium : ViewModel() {

    companion object {
        const val PAGE_1 = 0
        const val PAGE_2 = 1
        const val PAGE_3 = 2
    }

    val currentlyShown = MutableLiveData(PAGE_1)
    fun setCurrentlyShown(value: Int) {
        currentlyShown.value = value
    }

    val removedCategoryList = ObservableArrayList<CategoryStatus>()
    val addedCategoryList = ObservableArrayList<CategoryStatus>()
    val newCategoryList = ObservableArrayList<CategoryStatus>()
}