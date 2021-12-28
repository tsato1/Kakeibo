package com.kakeibo.feature_settings.presentation.custom_category_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.use_cases.CustomCategoryUseCases
import com.kakeibo.feature_settings.domain.util.CustomCategoryListOrder
import com.kakeibo.util.UtilCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCategoryListViewModel @Inject constructor(
    private val customCategoryUseCases: CustomCategoryUseCases
) : ViewModel() {

    private val _customCategoryListState = mutableStateOf(CustomCategoryListState())
    val customCategoryListState: State<CustomCategoryListState> = _customCategoryListState

    private var recentlyDeletedCustomCategory: CategoryModel? = null

    private var loadCustomCategoriesJob: Job? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadCustomCategories(CustomCategoryListOrder.Name)
    }

    fun onEvent(event: CustomCategoryListEvent) {
        when (event) {
            is CustomCategoryListEvent.CreateNew -> {
            }
            is CustomCategoryListEvent.Reorder -> {
                if (customCategoryListState.value.listOrder::class == event.listOrder::class)
                    return

                reorderList(event.listOrder)
            }
            is CustomCategoryListEvent.Delete -> {
                viewModelScope.launch {
                    customCategoryUseCases.deleteCategoryUseCase(event.categoryModel)
                    recentlyDeletedCustomCategory = event.categoryModel
                }
            }
            is CustomCategoryListEvent.Restore -> {
                viewModelScope.launch {
                    customCategoryUseCases.insertCustomCategoryUseCase(
                        recentlyDeletedCustomCategory ?: return@launch
                    )
                    recentlyDeletedCustomCategory = null
                }
            }
            is CustomCategoryListEvent.ToggleOrderSection -> {
                _customCategoryListState.value = customCategoryListState.value.copy(
                    isOrderSectionVisible = !customCategoryListState.value.isOrderSectionVisible
                )
            }
        }
    }

    fun getNewCode(): Int {
        var index = UtilCategory.CUSTOM_CATEGORY_CODE_START
        customCategoryListState.value.customCategoryList
            .sortedBy { it.code }
            .forEach {
                if (index == it.code)
                    index += 1
                else
                    return index
            }
        return index
    }

    private fun loadCustomCategories(listOrder: CustomCategoryListOrder) {
        loadCustomCategoriesJob?.cancel()
        loadCustomCategoriesJob = viewModelScope.launch {
            customCategoryUseCases.getAllCustomCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _customCategoryListState.value = customCategoryListState.value.copy(
                                customCategoryList = result.data ?: emptyList(),
                                listOrder = listOrder,
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _customCategoryListState.value = customCategoryListState.value.copy(
                                customCategoryList = result.data ?: emptyList(),
                                listOrder = listOrder,
                                isLoading = false
                            )
                            _eventFlow.emit(UiEvent.ShowToast(result.message ?: "Unknown Error"))
                        }
                        is Resource.Loading -> {
                            _customCategoryListState.value = customCategoryListState.value.copy(
                                customCategoryList = result.data ?: emptyList(),
                                listOrder = listOrder,
                                isLoading = true
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun reorderList(listOrder: CustomCategoryListOrder) {
        when (listOrder) {
            is CustomCategoryListOrder.Name -> {
                _customCategoryListState.value.customCategoryList.sortedBy { it.name }
            }
            is CustomCategoryListOrder.Code -> {
                _customCategoryListState.value.customCategoryList.sortedBy { it.code }
            }
            is CustomCategoryListOrder.Color -> {
                _customCategoryListState.value.customCategoryList.sortedBy { it.color }
            }
        }

        _customCategoryListState.value = customCategoryListState.value.copy(
            listOrder = listOrder
        )
    }

    sealed class UiEvent {
        data class ShowToast(val message: String): UiEvent()
    }

}