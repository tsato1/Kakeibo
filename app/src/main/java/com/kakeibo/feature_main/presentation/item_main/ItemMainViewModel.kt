package com.kakeibo.feature_main.presentation.item_main

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.use_cases.ItemUseCases
import com.kakeibo.feature_main.domain.use_cases.SearchUseCases
import com.kakeibo.feature_main.presentation.item_main.item_chart.ItemChartState
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItemListState
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ExpandableItem
import com.kakeibo.util.UtilCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemMainViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val searchUseCases: SearchUseCases,
    appPreferences: AppPreferences,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val dateFormatIndex = appPreferences.getDateFormatIndex()

    private val _expandableItemListState = mutableStateOf(ExpandableItemListState())
    val expandableItemListState: State<ExpandableItemListState> = _expandableItemListState

    private val _itemChartState = mutableStateOf(ItemChartState())
    val itemChartState: State<ItemChartState> = _itemChartState

    private var recentlyDeletedDisplayedItemModel: DisplayedItemModel? = null

    private var getItemsJob: Job? = null

    private val _searchId = mutableStateOf(savedStateHandle.get("searchId") ?: -1L)
    val searchId: State<Long> = _searchId

    private val _searchModel = mutableStateOf(SearchModel())
    val searchModel: State<SearchModel?> = _searchModel


    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        Log.d("asdf","init in MainViewModel "+searchId.value.toString())
        if (_searchId.value == -1L) {
            loadThisMonthData()
        }
        else {
            onEvent(ItemMainEvent.LoadItems(_searchId.value))
        }
    }

    private fun loadThisMonthData() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val year = String.format("%04d", cal.get(Calendar.YEAR))
        val month = String.format("%02d", cal.get(Calendar.MONTH) + 1)
        val lastDayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
        loadItems(
            SearchModel(
                fromDate = "$year-$month-01",
                toDate = "$year-$month-$lastDayOfMonth"
            )
        )
    }

    fun onEvent(event: ItemMainEvent) {
        when (event) {
            is ItemMainEvent.DateChanged -> {
                val year = String.format("%04d", event.value.year)
                val month = String.format("%02d", event.value.monthNumber)
                val lastDayOfMonth = event.value.dayOfMonth
                loadItems(
                    SearchModel(
                        fromDate = "$year-$month-01",
                        toDate = "$year-$month-$lastDayOfMonth"
                    )
                )
            }
            is ItemMainEvent.DeleteItem -> {
                viewModelScope.launch {
                    itemUseCases.deleteItemUseCase(event.displayedItemModel)
                    recentlyDeletedDisplayedItemModel = event.displayedItemModel
                }
            }
            is ItemMainEvent.RestoreItem -> {
                viewModelScope.launch {
                    itemUseCases.insertItemUseCase( // todo catch exception
                        recentlyDeletedDisplayedItemModel ?: return@launch
                    )
                    recentlyDeletedDisplayedItemModel = null
                }
            }
            is ItemMainEvent.LoadItems -> {
                savedStateHandle.set("searchId", event.searchId)
                _searchId.value = event.searchId
                viewModelScope.launch {
                    _searchModel.value = searchUseCases.getSearchByIDUseCase(event.searchId)
                    searchModel.value?.let {
                        loadItems(it)
                    }
                }
            }
            is ItemMainEvent.ExitSearchMode -> {
                savedStateHandle.set("searchId", -1L)
                _searchId.value = -1L
                loadThisMonthData()
            }
            else -> {}
        }
    }

    private fun loadItems(searchModel: SearchModel) {
        Log.d("asdf","loadItems in MainViewModel : $searchId.value  "+searchModel.toQuery())
        viewModelScope.launch {
            getItemsJob?.cancel()
            getItemsJob = itemUseCases.getSpecificItemsUseCase(searchModel.toQuery(), searchModel.toArgs())
                .onEach { result ->
                    /*
                    Used in ItemListScreen
                     */
                    val expandableItemList = result.data
                        ?.groupBy { it.eventDate }
                        ?.map { entry ->
                            ExpandableItem(
                                ExpandableItem.Parent(
                                    entry.key,
                                    entry.value.map { it.amount.toLong() }.filter { it > 0 }.sum().toString(),
                                    entry.value.map { it.amount.toLong() }.filter { it < 0 }.sum().toString()
                                ),
                                entry.value
                            )
                        } ?: emptyList()

                    /*
                    Used in ItemChartScreen
                     */
                    val incomeTotal = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.sumOf { it.amount.toLong() } ?: 0L

                    val expenseTotal = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                        ?.sumOf { it.amount.toLong() } ?: 0L

                    val incomeCategoryList = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.groupingBy { Triple(it.categoryCode, it.categoryDrawable, it.categoryImage) }
                        ?.reduce { _, acc, ele ->
                            val sum = acc.amount.toLong() + ele.amount.toLong()
                            acc.copy(
                                amount = sum.toString()
                            )
                        }
                        ?.values?.toList()
                        ?.sortedByDescending { it.amount.toLong() } ?: emptyList()

                    val expenseCategoryList = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                        ?.groupingBy { Triple(it.categoryCode, it.categoryDrawable, it.categoryImage) }
                        ?.reduce { _, acc, ele ->
                            val sum = acc.amount.toLong() + ele.amount.toLong()
                            acc.copy(
                                amount = sum.toString()
                            )
                        }
                        ?.values?.toList()
                        ?.sortedBy { it.amount.toLong() } ?: emptyList()

                    val itemMapByCategoryIncome = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.groupBy { it.categoryCode } ?: emptyMap()

                    val itemMapByCategoryExpense = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                        ?.groupBy { it.categoryCode } ?: emptyMap()

                    when (result) {
                        is Resource.Success -> {
                            _expandableItemListState.value = expandableItemListState.value.copy(
                                expandableItemList = expandableItemList,
                                isLoading = false
                            )
                            _itemChartState.value = itemChartState.value.copy(
                                incomeTotal = incomeTotal,
                                expenseTotal = expenseTotal,
                                incomeList = incomeCategoryList,
                                expenseList = expenseCategoryList,
                                incomeMap = itemMapByCategoryIncome,
                                expenseMap = itemMapByCategoryExpense,
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _expandableItemListState.value = expandableItemListState.value.copy(
                                expandableItemList = expandableItemList,
                                isLoading = false
                            )
                            _itemChartState.value = itemChartState.value.copy(
                                incomeTotal = incomeTotal,
                                expenseTotal = expenseTotal,
                                incomeList = incomeCategoryList,
                                expenseList = expenseCategoryList,
                                incomeMap = itemMapByCategoryIncome,
                                expenseMap = itemMapByCategoryExpense,
                                isLoading = false
                            )
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    result.message ?: "At ItemMainViewModel: Unknown error."
                                )
                            )
                        }
                        is Resource.Loading -> {
                            _expandableItemListState.value = expandableItemListState.value.copy(
                                expandableItemList = expandableItemList,
                                isLoading = true
                            )
                            _itemChartState.value = itemChartState.value.copy(
                                incomeTotal = incomeTotal,
                                expenseTotal = expenseTotal,
                                incomeList = incomeCategoryList,
                                expenseList = expenseCategoryList,
                                incomeMap = itemMapByCategoryIncome,
                                expenseMap = itemMapByCategoryExpense,
                                isLoading = true
                            )
                        }
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
    }

}