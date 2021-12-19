package com.kakeibo.feature_settings.presentation.category_reorder.presentation

import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CategoryRearrangeViewModel @Inject constructor(
    private val displayedCategoryUseCases: DisplayedCategoryUseCases
) : BaseViewModel() {

//    private val _displayedCategoryState = mutableStateOf(DisplayedCategoryState())
//    val displayedCategoryState: State<DisplayedCategoryState> = _displayedCategoryState

    init {
//        viewModelScope.launch {
//            displayedCategoryUseCases.getDisplayedCategoriesUseCase().also { categories ->
//                _displayedCategoryState.value = displayedCategoryState.value.copy(
//                    displayedCategoryList = categories
//                )
//            }
//        }
    }

}