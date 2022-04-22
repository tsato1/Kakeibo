package com.kakeibo.feature_settings.presentation.category_rearrange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.data.local.entities.CategoryDspEntity
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryRearrangeViewModel @Inject constructor(
    private val categoryRearrangeUseCases: CategoryRearrangeUseCases,
    appPreferences: AppPreferences
) : ViewModel() {

    val numColumns = appPreferences.getNumColumns()

    private val _categoryRearrangeState = mutableStateOf(CategoryRearrangeState())
    val categoryRearrangeState: State<CategoryRearrangeState> = _categoryRearrangeState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var loadDisplayedCategoryListJob: Job? = null
    var loadNonDisplayedCategoryListJob: Job? = null

    init {
        loadCategoryLists()
    }

    fun onEvent(event: CategoryRearrangeEvent) {
        when (event) {
            is CategoryRearrangeEvent.Add -> {
                _categoryRearrangeState.value.finalCategoryList.add(event.categoryModel)
            }
            is CategoryRearrangeEvent.Remove -> {
                val iterator = _categoryRearrangeState.value.finalCategoryList.iterator()
                while (iterator.hasNext()) {
                    val currCategory = iterator.next()
                    if (event.categoryModel.code == currCategory.code) {
                        iterator.remove()
                    }
                }
            }
            is CategoryRearrangeEvent.SaveAndReorder -> {
                viewModelScope.launch {
                    try {
                        categoryRearrangeUseCases.updateDisplayedCategoriesUseCase(
                            categoryRearrangeState.value.finalCategoryList
                        )
                        _eventFlow.emit(UiEvent.SaveAndReorder)
                    }
                    catch (e: CategoryDspEntity.InvalidCategoryDspException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(
                                    e.message ?: "Error: Couldn't save Category Rearrangement."
                                )
                            )
                        )
                    }
                }
            }
            is CategoryRearrangeEvent.SaveWithoutReorder -> {
                viewModelScope.launch {
                    try {
                        categoryRearrangeUseCases.updateDisplayedCategoriesUseCase(
                            categoryRearrangeState.value.finalCategoryList
                        )
                        _eventFlow.emit(UiEvent.SaveWithoutReorder)
                    }
                    catch (e: CategoryDspEntity.InvalidCategoryDspException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(
                                    e.message ?: "Error: Couldn't save Category Rearrangement."
                                )
                            )
                        )
                    }
                }
            }
        }
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
                                finalCategoryList = result.data?.toMutableList() ?: mutableListOf(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                finalCategoryList = result.data?.toMutableList() ?: mutableListOf(),
                                isDisplayedCategoryListLoading = false,
                                isNonDisplayedCategoryListLoading = false
                            )
                            _eventFlow.emit(
                                UiEvent.ShowToast(
                                    UiText.DynamicString(result.message ?: "Unknown Error")
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _categoryRearrangeState.value = categoryRearrangeState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                finalCategoryList = result.data?.toMutableList() ?: mutableListOf(),
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
                            _eventFlow.emit(
                                UiEvent.ShowToast(
                                    UiText.DynamicString(result.message ?: "Unknown Error")
                                )
                            )
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

    sealed class UiEvent {
        data class ShowSnackbar(val message: UiText): UiEvent()
        data class ShowToast(val message: UiText): UiEvent()
        object SaveAndReorder: UiEvent()
        object SaveWithoutReorder: UiEvent()
    }
}