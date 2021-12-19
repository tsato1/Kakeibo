package com.kakeibo.feature_settings.presentation.category_rearrange.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.util.Resource
import com.kakeibo.core.util.UiEvent
import com.kakeibo.feature_main.presentation.common.BaseViewModel
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryRearrangeViewModel @Inject constructor(
    private val categoryRearrangeUseCases: CategoryRearrangeUseCases
) : BaseViewModel() {

    private val _categoryRearrangeState = mutableStateOf(CategoryRearrangeState())
    val categoryRearrangeState: State<CategoryRearrangeState> = _categoryRearrangeState

    private val _eventFlow = MutableSharedFlow<UiEvent>()

    var loadDisplayedCategoryListJob: Job? = null
    var loadNonDisplayedCategoryListJob: Job? = null

    init {
        loadCategoryLists()
    }

    fun onEvent() {

    }

    private fun loadCategoryLists() {
        loadDisplayedCategoryListJob?.cancel()
        loadDisplayedCategoryListJob = viewModelScope.launch {
            categoryRearrangeUseCases.getDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                            _eventFlow.emit(UiEvent.ShowToast(result.message ?: "Unknown Error"))
                        }
                        is Resource.Loading -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = true,
                                isNonDisplayedCategoryListLoading = false
                            )
                        }
                    }
                }
                .launchIn(this)
        }
        loadNonDisplayedCategoryListJob?.cancel()
        loadNonDisplayedCategoryListJob = viewModelScope.launch {
            categoryRearrangeUseCases.getNonDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                nonDisplayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                nonDisplayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                            _eventFlow.emit(UiEvent.ShowToast(result.message ?: "Unknown Error"))
                        }
                        is Resource.Loading -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                nonDisplayedCategoryList = result.data ?: emptyList(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = true
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

}