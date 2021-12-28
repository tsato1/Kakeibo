package com.kakeibo.feature_settings.presentation.category_reorder

import androidx.lifecycle.*
import com.kakeibo.core.util.Resource
import com.kakeibo.core.presentation.BaseViewModel
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRearrangeUseCases: CategoryRearrangeUseCases
) : BaseViewModel() {

    private val _displayedCategories = MutableLiveData<List<CategoryModel>>()
    val displayedCategories: LiveData<List<CategoryModel>> = _displayedCategories

    var loadDisplayedCategoryListJob: Job? = null

    init {
        loadDisplayedCategoryListJob?.cancel()
        loadDisplayedCategoryListJob = viewModelScope.launch {
            categoryRearrangeUseCases.getDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _displayedCategories.value = result.data ?: emptyList()
                        }
                        is Resource.Error -> {
                            _displayedCategories.value = result.data ?: emptyList()
                            // send error
                        }
                        is Resource.Loading -> {
                            _displayedCategories.value = result.data ?: emptyList()
                        }
                    }
                }
                .launchIn(this)
        }
    }

    fun onSave(list: List<CategoryModel>) {
        viewModelScope.launch {
            categoryRearrangeUseCases.updateDisplayedCategoriesUseCase(list)
        }
    }

}