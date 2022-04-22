package com.kakeibo.feature_main.presentation.item_search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.domain.use_cases.SearchUseCases
import com.kakeibo.feature_main.presentation.item_detail.item_input.DisplayedCategoryListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemSearchViewModel @Inject constructor(
    private val displayedCategoryUseCases: DisplayedCategoryUseCases,
    private val searchUseCases: SearchUseCases,
    appPreferences: AppPreferences
) : ViewModel() {

    val dateFormatIndex = appPreferences.getDateFormatIndex()
    val fractionDigits = appPreferences.getFractionDigits()

    /* default search criteria that show up in dialog when the user taps 'Add' fab */
    private var defaultSearchCriteria = mutableListOf(
        SearchCriterion.TypeDateRange(),
        SearchCriterion.TypeAmount(),
        SearchCriterion.TypeCategory(),
        SearchCriterion.TypeMemo())
    private val _defaultSearchCriteriaState = mutableStateOf(defaultSearchCriteria.toList())
    val defaultSearchCriteriaState: State<List<SearchCriterion>> = _defaultSearchCriteriaState

    /* search criteria chosen by the user */
    private val chosenSearchCriteria = mutableListOf<SearchCriterion>()
    private val _chosenSearchCriteriaState = mutableStateOf(chosenSearchCriteria.toList())
    val chosenSearchCriteriaState: State<List<SearchCriterion>> = _chosenSearchCriteriaState

    /* search cards */
    private val _searchCardDateRangeState = mutableStateOf(SearchCardDateRangeState())
    val searchCardDateRangeState: State<SearchCardDateRangeState> = _searchCardDateRangeState

    private val _searchCardAmountState = mutableStateOf(SearchCardAmountState())
    val searchCardAmountState: State<SearchCardAmountState> = _searchCardAmountState

    private val _searchCardCategoryState = mutableStateOf(SearchCardCategoryState())
    val searchCardCategoryState: State<SearchCardCategoryState> = _searchCardCategoryState

    private val _searchCardMemoState = mutableStateOf(SearchCardMemoState())
    val searchCardMemoState: State<SearchCardMemoState> = _searchCardMemoState

    /* displayed categories that show up in category card */
    private val _displayedCategoryListState = mutableStateOf(DisplayedCategoryListState())
    val displayedCategoryListState: State<DisplayedCategoryListState> = _displayedCategoryListState

    /* other */
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var loadDisplayedCategoryListJob: Job? = null

    init {
        loadCategories()
    }

    fun onEvent(event: ItemSearchEvent) {
        when (event) {
            is ItemSearchEvent.CriterionAdded -> {
                defaultSearchCriteria.remove(event.criterion)
                chosenSearchCriteria.add(event.criterion)
                _defaultSearchCriteriaState.value = defaultSearchCriteria
                _chosenSearchCriteriaState.value = chosenSearchCriteria
            }
            is ItemSearchEvent.CriterionRemoved -> {
                defaultSearchCriteria.add(event.criterion)
                chosenSearchCriteria.remove(event.criterion)
                _defaultSearchCriteriaState.value = defaultSearchCriteria
                _chosenSearchCriteriaState.value = chosenSearchCriteria
            }
            is ItemSearchEvent.DateFromSelected -> {
                _searchCardDateRangeState.value = searchCardDateRangeState.value.copy(
                    from = event.from
                )
            }
            is ItemSearchEvent.DateToSelected -> {
                _searchCardDateRangeState.value = searchCardDateRangeState.value.copy(
                    to = event.to
                )
            }
            is ItemSearchEvent.AmountFromEntered -> {
                _searchCardAmountState.value = searchCardAmountState.value.copy(
                    from = event.from
                )
            }
            is ItemSearchEvent.AmountToEntered -> {
                _searchCardAmountState.value = searchCardAmountState.value.copy(
                    to = event.to
                )
            }
            is ItemSearchEvent.CategorySelected -> {
                _searchCardCategoryState.value = searchCardCategoryState.value.copy(
                    categoryModel = event.categoryModel
                )
            }
            is ItemSearchEvent.MemoEntered -> {
                _searchCardMemoState.value = searchCardMemoState.value.copy(
                    memo = event.memo
                )
            }
            is ItemSearchEvent.Search -> {
                viewModelScope.launch {
                    val searchModel = SearchModel()
                        .also { it._id = 1 } // todo: if paid, store multiple entries for search history
                    for (chosenSearchCriterion in chosenSearchCriteria) {
                        when (chosenSearchCriterion) {
                            is SearchCriterion.TypeDateRange -> {
                                searchModel.fromDate = _searchCardDateRangeState.value.from.toString()
                                searchModel.toDate = _searchCardDateRangeState.value.to.toString()
                            }
                            is SearchCriterion.TypeAmount -> {
                                searchModel.fromAmount = _searchCardAmountState.value.from
                                searchModel.toAmount = _searchCardAmountState.value.to
                            }
                            is SearchCriterion.TypeCategory -> {
                                searchModel.categoryCode =
                                    _searchCardCategoryState.value.categoryModel?.code
                                searchModel.categoryName =
                                    _searchCardCategoryState.value.categoryModel?.name
                            }
                            is SearchCriterion.TypeMemo -> {
                                searchModel.memo = _searchCardMemoState.value.memo
                            }
                        }
                    }

                    searchUseCases.insertSearchUseCase(
                        searchModel = searchModel
                    ).also { searchId ->
                        _eventFlow.emit(UiEvent.Search(searchId))
                    }
                }
            }
            else -> {}
        }
    }

    private fun loadCategories() {
        loadDisplayedCategoryListJob?.cancel()
        loadDisplayedCategoryListJob = viewModelScope.launch {
            displayedCategoryUseCases.getDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _displayedCategoryListState.value = displayedCategoryListState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _displayedCategoryListState.value = displayedCategoryListState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isLoading = false
                            )
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    UiText.DynamicString(result.message ?: "Unknown Error")
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _displayedCategoryListState.value = displayedCategoryListState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isLoading = true
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: UiText): UiEvent()
        data class Search(val searchId: Long): UiEvent()
    }
}