package com.kakeibo.feature_main.presentation.item_main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedItemUseCases
import com.kakeibo.feature_main.domain.use_cases.SearchUseCases
import com.kakeibo.feature_main.presentation.common.BaseViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.CalendarItem
import com.kakeibo.feature_main.presentation.item_main.item_calendar.CalendarItemListState
import com.kakeibo.feature_main.presentation.item_main.item_chart.ItemChartState
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItem
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItemListState
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilDate.isWithinMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import javax.inject.Inject

@HiltViewModel
class ItemMainViewModel @Inject constructor(
    private val displayedItemUseCases: DisplayedItemUseCases,
    private val searchUseCases: SearchUseCases,
    appPreferences: AppPreferences,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val dateFormatIndex = appPreferences.getDateFormatIndex()

    private val _searchId = mutableStateOf(savedStateHandle["searchId"] ?: 0L)
    val searchId: State<Long> = _searchId

    private val _searchModel = mutableStateOf(SearchModel())
    val searchModel: State<SearchModel?> = _searchModel

    private val _calendarFromDate = mutableStateOf(LocalDate(
        UtilDate.getTodaysLocalDate().year,
        UtilDate.getTodaysLocalDate().monthNumber,
        1
    ))
    val calendarFromDate: State<LocalDate> = _calendarFromDate
    private val _calendarToDate = mutableStateOf(LocalDate(
        UtilDate.getTodaysLocalDate().year,
        UtilDate.getTodaysLocalDate().monthNumber,
        UtilDate.getLastDateOfMonth(UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB))
    ))
    val calendarToDate: State<LocalDate> = _calendarToDate

    private val _expandableItemListState = mutableStateOf(ExpandableItemListState())
    val expandableItemListState: State<ExpandableItemListState> = _expandableItemListState

    private val _calendarItemListState = mutableStateOf(CalendarItemListState())
    val calendarItemListState: State<CalendarItemListState> = _calendarItemListState

    private val _itemChartState = mutableStateOf(ItemChartState())
    val itemChartState: State<ItemChartState> = _itemChartState

    private var recentlyDeletedDisplayedItemModel: DisplayedItemModel? = null

    private var getItemsJob: Job? = null



    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (_searchId.value == 0L) {
            loadThisMonthData()
        }
        else {
            onEvent(ItemMainEvent.LoadItems(_searchId.value))
        }
    }

    private fun loadThisMonthData() {
        val today = UtilDate.getTodaysLocalDate()
        val remainingDays = UtilDate.getRemainingDays(today.toYMDString(UtilDate.DATE_FORMAT_DB))

        updateLocalEventDate(today.toYMDString(UtilDate.DATE_FORMAT_DB))

        _calendarFromDate.value = LocalDate(
            today.year, today.monthNumber, 1
        ).minus(UtilDate.getFirstDayOfMonth(today.toYMDString(UtilDate.DATE_FORMAT_DB)), DateTimeUnit.DAY)
        _calendarToDate.value = LocalDate(
            today.year, today.monthNumber, 1
        ) + DatePeriod(months = 1) - DatePeriod(days = 1) + DatePeriod(days = remainingDays)

        loadItems(
            searchModel = SearchModel(
                fromDate = calendarFromDate.value.toYMDString(UtilDate.DATE_FORMAT_DB),
                toDate = calendarToDate.value.toYMDString(UtilDate.DATE_FORMAT_DB)
            )
        )
    }

    override fun onDateChanged() {
        val date = localEventDate.value

        val firstDayOfMonth = UtilDate.getFirstDayOfMonth(date)
        val remainingDays = UtilDate.getRemainingDays(date)

        val localDate = date.toLocalDate()
        _calendarFromDate.value = LocalDate(
            localDate.year, localDate.monthNumber, 1
        ).minus(firstDayOfMonth, DateTimeUnit.DAY)
        _calendarToDate.value = LocalDate(
            localDate.year, localDate.monthNumber, 1
        ) + DatePeriod(months = 1) - DatePeriod(days = 1) + DatePeriod(days = remainingDays)

        loadItems(
            SearchModel(
                fromDate = calendarFromDate.value.toYMDString(UtilDate.DATE_FORMAT_DB),
                toDate = calendarToDate.value.toYMDString(UtilDate.DATE_FORMAT_DB)
            )
        )
    }

    fun onEvent(event: ItemMainEvent) {
        when (event) {
            is ItemMainEvent.DeleteItem -> {
                viewModelScope.launch {
                    displayedItemUseCases.deleteItemUseCase(event.displayedItemModel)
                    recentlyDeletedDisplayedItemModel = event.displayedItemModel
                }
            }
            is ItemMainEvent.RestoreItem -> {
                viewModelScope.launch {
                    displayedItemUseCases.insertItemUseCase( // todo catch exception
                        recentlyDeletedDisplayedItemModel ?: return@launch
                    )
                    recentlyDeletedDisplayedItemModel = null
                }
            }
            is ItemMainEvent.LoadItems -> {
                savedStateHandle["searchId"] = event.searchId
                _searchId.value = event.searchId
                viewModelScope.launch {
                    _searchModel.value = searchUseCases.getSearchByIDUseCase(event.searchId)
                    searchModel.value?.let {
                        loadItems(it)
                    }
                }
            }
            is ItemMainEvent.ExitSearchMode -> {
                savedStateHandle["searchId"] = 0L
                _searchId.value = 0L
                loadThisMonthData()
            }
            else -> {}
        }
    }

    private fun loadItems(searchModel: SearchModel) {
        _searchModel.value = searchModel
        viewModelScope.launch {
            getItemsJob?.cancel()
            getItemsJob = displayedItemUseCases.getSpecificItemsUseCase(searchModel.toQuery(), searchModel.toArgs())
                .onEach { result ->
                    /*
                    Used in ItemListScreen
                     */
                    val expandableItemList = result.data
                        ?.groupBy { it.eventDate }
                        ?.filter { it.key.isWithinMonth(localEventDate.value) }
                        ?.map { entry ->
                            ExpandableItem(
                                ExpandableItem.Parent(
                                    entry.key,
                                    entry.value
                                        .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                                        .sumOf { it.amount.toLong() }.toString(),
                                    entry.value
                                        .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                                        .sumOf { it.amount.toLong() }.toString()
                                ),
                                entry.value
                            )
                        } ?: emptyList()

                    /*
                    Used in ItemCalendarScreen
                     */
                    val calendarItemList = result.data
                        ?.groupBy { it.eventDate }
                        ?.map { entry ->
                            CalendarItem(
                                CalendarItem.Parent(
                                    entry.key,
                                    entry.value
                                        .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                                        .sumOf { it.amount.toLong() }.toString(),
                                    entry.value
                                        .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                                        .sumOf { it.amount.toLong() }.toString()
                                ),
                                entry.value
                            )
                        }
                        ?.toMutableList() ?: mutableListOf()

                    var iDate = calendarFromDate.value
                    var index = 0
                    while (iDate <= calendarToDate.value) {
                        if (calendarItemList.isEmpty() || index >= calendarItemList.size) {
                            calendarItemList.add(
                                CalendarItem(
                                    CalendarItem.Parent(
                                        iDate.toYMDString(UtilDate.DATE_FORMAT_DB),
                                        "0",
                                        "0"
                                    ),
                                    emptyList()
                                )
                            )
                        }
                        else if (iDate < calendarItemList[index].parent.date.toLocalDate()) {
                            calendarItemList.add(
                                index,
                                CalendarItem(
                                    CalendarItem.Parent(
                                        iDate.toYMDString(UtilDate.DATE_FORMAT_DB),
                                        "0",
                                        "0"
                                    ),
                                    emptyList()
                                )
                            )
                        }
                        index += 1
                        iDate += DatePeriod(days = 1)
                    }

                    /*
                    Used in ItemChartScreen
                     */
                    val incomeTotal = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
                        ?.sumOf { it.amount.toLong() } ?: 0L

                    val expenseTotal = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
                        ?.sumOf { it.amount.toLong() } ?: 0L

                    val incomeCategoryList = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
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
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
                        ?.groupingBy { Triple(it.categoryCode, it.categoryDrawable, it.categoryImage) }
                        ?.reduce { _, acc, ele ->
                            val sum = acc.amount.toLong() + ele.amount.toLong()
                            acc.copy(
                                amount = sum.toString()
                            )
                        }
                        ?.values?.toList()
                        ?.sortedByDescending { it.amount.toLong() } ?: emptyList()

                    val itemMapByCategoryIncome = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
                        ?.groupBy { it.categoryCode } ?: emptyMap()

                    val itemMapByCategoryExpense = result.data
                        ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                        ?.filter { it.eventDate.isWithinMonth(localEventDate.value) }
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
                            _calendarItemListState.value = calendarItemListState.value.copy(
                                calendarItemList = calendarItemList,
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
                            _calendarItemListState.value = calendarItemListState.value.copy(
                                calendarItemList = calendarItemList,
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
                            _calendarItemListState.value = calendarItemListState.value.copy(
                                calendarItemList = calendarItemList,
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