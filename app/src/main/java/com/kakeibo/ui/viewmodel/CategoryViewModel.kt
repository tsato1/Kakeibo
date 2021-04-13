package com.kakeibo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.kakeibo.SubApp
import com.kakeibo.data.Category
import com.kakeibo.data.DataRepository
import com.kakeibo.util.UtilCategory

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository = (application as SubApp).repository

    val all: LiveData<List<Category>> = repository.categories
    val allMap: LiveData<Map<Int, Category>> = Transformations.map(all) { all ->
        all.associateBy( { it.code }, { it })
    }

    val dsp: LiveData<List<Category>> = repository.categoriesDisplayed
    val nonDsp: LiveData<List<Category>> = repository.categoriesNotDisplayed
    val custom: LiveData<List<Category>> = Transformations.map(all) { all ->
        all.filter { it.code >= UtilCategory.CUSTOM_CATEGORY_CODE_START }
    }

    fun insert(category: Category) {
        repository.insertCategory(category)
    }

    fun delete(id: Long) {
        repository.deleteCategory(id)
    }

    /* for custom category */
    fun getCodeForNewCustomCategory(): Int {
        all.value?.let { categories ->
            val codes = categories.map { it.code }
                    .filter { it >= UtilCategory.CUSTOM_CATEGORY_CODE_START }
                    .sortedBy { it }

            var i = UtilCategory.CUSTOM_CATEGORY_CODE_START
            for (code in codes) {
                if (code == i) {
                    i++
                    continue
                }
                else {
                    return i
                }
            }
            return i
        }
        return UtilCategory.CUSTOM_CATEGORY_CODE_START
    }

    fun canCreateNewCustomCategory(): Int {
        all.value?.let {
            val newCode = getCodeForNewCustomCategory()
            if (newCode >= UtilCategory.CUSTOM_CATEGORY_CODE_START + UtilCategory.NUM_MAX_CUSTOM_CATEGORY) {
                return -2 // failure because the number of custom categories has reached max
            }
            return 1 // success
        }
        return -1 // failure because all.value == null
    }

    fun isCategoryAlreadyUsed(code: Int): Boolean {
        allMap.value?.let {
            return it.contains(code)
        }
        return false
    }
}