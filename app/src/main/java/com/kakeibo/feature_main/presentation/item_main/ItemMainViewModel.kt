package com.kakeibo.feature_main.presentation.item_main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.models.SearchModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedItemUseCases
import com.kakeibo.feature_main.domain.use_cases.SearchUseCases
import com.kakeibo.feature_main.presentation.common.DateViewModel
import com.kakeibo.feature_main.presentation.item_main.item_calendar.CalendarItem
import com.kakeibo.feature_main.presentation.item_main.item_calendar.CalendarItemListState
import com.kakeibo.feature_main.presentation.item_main.item_chart.ItemChartState
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItem
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItemListState
import com.kakeibo.feature_main.presentation.item_main.item_list.containsAt
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.of
import com.kakeibo.util.UtilDate.plus
import com.kakeibo.util.UtilDate.toCalendar
import com.kakeibo.util.UtilDate.toYMDString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ItemMainViewModel @Inject constructor(
    private val displayedItemUseCases: DisplayedItemUseCases,
    private val searchUseCases: SearchUseCases,
    private val appPreferences: AppPreferences,
    private val savedStateHandle: SavedStateHandle
) : DateViewModel(savedStateHandle) {

    private val _dateFormatIndexState = mutableStateOf(0)
    val dateFormatIndexState: State<Int> = _dateFormatIndexState
    private val _fractionDigitsIndexState = mutableStateOf(0)
    val fractionDigitsIndexState: State<Int> = _fractionDigitsIndexState

    private val _searchId = mutableStateOf(savedStateHandle["searchId"] ?: 0L)
    val searchId: State<Long> = _searchId
    private val _searchModel = mutableStateOf(SearchModel())
    val searchModel: State<SearchModel?> = _searchModel

    /* used for expanding the expandableList and scrolling to this item*/
    private val _focusItemIdState = mutableStateOf(savedStateHandle["focusItemId"] ?: -1L)
    val focusItemIdState: State<Long> = _focusItemIdState

    private val _calendarFromDate = mutableStateOf(Calendar.getInstance())
    val calendarFromDate: State<Calendar> = _calendarFromDate
    private val _calendarToDate = mutableStateOf(Calendar.getInstance())
    val calendarToDate: State<Calendar> = _calendarToDate

    private val _expandableItemListState = MutableStateFlow(ExpandableItemListState())
    val expandableItemListState: StateFlow<ExpandableItemListState> = _expandableItemListState

    private val _calendarItemListState = mutableStateOf(CalendarItemListState())
    val calendarItemListState: State<CalendarItemListState> = _calendarItemListState

    private val _itemChartState = mutableStateOf(ItemChartState())
    val itemChartState: State<ItemChartState> = _itemChartState

    private var recentlyDeletedDisplayedItemModel: DisplayedItemModel? = null

    private var getItemsJob: Job? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        onEvent(ItemMainEvent.LoadItems(_searchId.value, cal.value, _focusItemIdState.value))
    }

    fun setSharedPreferencesStates() {
        _dateFormatIndexState.value = appPreferences.getDateFormatIndex()
        _fractionDigitsIndexState.value = appPreferences.getFractionDigitsIndex()
    }

    private fun loadThisMonthData() {
        val today = cal.value
        val firstDayOfMonth = UtilDate.getFirstDayOfMonth(
            today.toYMDString(UtilDate.DATE_FORMAT_DB),
            UtilDate.DATE_FORMAT_DB
        )
        val remainingDays = UtilDate.getRemainingDays(today.toYMDString(UtilDate.DATE_FORMAT_DB))

        _calendarFromDate.value = Calendar.getInstance()
            .of(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1)
            .plus(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        _calendarToDate.value = Calendar.getInstance()
            .of(today.get(Calendar.YEAR), today.get(Calendar.MONTH), 1)
            .plus(Calendar.MONTH, 1)
            .plus(Calendar.DAY_OF_MONTH, -1)
            .plus(Calendar.DAY_OF_MONTH, remainingDays)

        viewModelScope.launch {
            loadItems(
                searchModel = SearchModel(
                    fromDate = calendarFromDate.value.toYMDString(UtilDate.DATE_FORMAT_DB),
                    toDate = calendarToDate.value.toYMDString(UtilDate.DATE_FORMAT_DB)
                )
            )
        }
    }

    override fun onDateChanged() {
        /* reloads only when not in search mode */
        if (searchId.value == 0L) {
            val date = cal.value

            val firstDayOfMonth = UtilDate.getFirstDayOfMonth(
                date.toYMDString(UtilDate.DATE_FORMAT_DB),
                UtilDate.DATE_FORMAT_DB
            )
            val remainingDays = UtilDate.getRemainingDays(date.toYMDString(UtilDate.DATE_FORMAT_DB))

            _calendarFromDate.value = Calendar.getInstance()
                .of(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1)
                .plus(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

            _calendarToDate.value = Calendar.getInstance()
                .of(date.get(Calendar.YEAR), date.get(Calendar.MONTH), 1)
                .plus(Calendar.MONTH, 1)
                .plus(Calendar.DAY_OF_MONTH, -1)
                .plus(Calendar.DAY_OF_MONTH, remainingDays)

            viewModelScope.launch {
                loadItems(
                    SearchModel(
                        fromDate = calendarFromDate.value.toYMDString(UtilDate.DATE_FORMAT_DB),
                        toDate = calendarToDate.value.toYMDString(UtilDate.DATE_FORMAT_DB)
                    )
                )
            }
        }
    }

    fun onEvent(event: ItemMainEvent) {
        when (event) {
            is ItemMainEvent.DeleteItem -> {
                viewModelScope.launch {
                    displayedItemUseCases.deleteItemUseCase(
                        displayedItemModel = event.displayedItemModel,
                        syncWithRemote = kkbAppModelState.value.kkbAppModel.intVal3
                    )
                    recentlyDeletedDisplayedItemModel = event.displayedItemModel
                }
            }
            is ItemMainEvent.RestoreItem -> {
                viewModelScope.launch {
                    try {
                        displayedItemUseCases.insertItemUseCase(
                            displayedItemModel = recentlyDeletedDisplayedItemModel ?: return@launch,
                            syncWithRemote = kkbAppModelState.value.kkbAppModel.intVal3
                        )
                        recentlyDeletedDisplayedItemModel = null
                    } catch (e: ItemEntity.InvalidItemException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(e.message ?: "Error: Couldn't save the item..")
                            )
                        )
                    }
                }
            }
            is ItemMainEvent.LoadItems -> {
                savedStateHandle["searchId"] = event.searchId
                _searchId.value = event.searchId
                savedStateHandle["focusItemId"] = event.focusItemId
                _focusItemIdState.value = event.focusItemId

                if (event.searchId == 0L) { /* NOT in search mode */
                    updateLocalEventDate(event.focusDate.toYMDString(UtilDate.DATE_FORMAT_DB))
                    loadThisMonthData()
                } else { /* in search mode */
                    viewModelScope.launch {
                        _searchModel.value = searchUseCases.getSearchByIDUseCase(event.searchId)
                        searchModel.value?.let {
                            loadItems(it)
                        }
                    }
                }
            }
            is ItemMainEvent.ExitSearchMode -> {
                savedStateHandle["searchId"] = 0L
                _searchId.value = 0L
                resetToToday()
                savedStateHandle["focusItemId"] = -1L
                _focusItemIdState.value = -1L
                loadThisMonthData()
            }
        }
    }

    private suspend fun loadItems(searchModel: SearchModel) {
        _searchModel.value = searchModel
        getItemsJob?.cancel()
        getItemsJob = runBlocking {
            displayedItemUseCases.getSpecificItemsUseCase(
                query = searchModel.toQuery(),
                args = searchModel.toArgs(),
                syncWithRemote = kkbAppModelState.value.kkbAppModel.intVal3
            ).onEach { result ->
                /*
                Used in ItemListScreen
                 */
                val expandableItemList = result.data
                    ?.groupBy { it.eventDate }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.key.toCalendar().get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.map { entry ->
                        ExpandableItem(
                            ExpandableItem.Parent(
                                entry.key,
                                entry.value
                                    .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                                    .sumOf { it.amount.toBigDecimal() }.toString(),
                                entry.value
                                    .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                                    .sumOf { it.amount.toBigDecimal() }.toString(),
                                entry.value.containsAt(focusItemIdState.value)
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
                                    .sumOf { it.amount.toBigDecimal() }.toString(),
                                entry.value
                                    .filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                                    .sumOf { it.amount.toBigDecimal() }.toString()
                            ),
                            entry.value
                        )
                    }
                    ?.toMutableList() ?: mutableListOf()

                val iDate = Calendar.getInstance().of(
                    year = calendarFromDate.value.get(Calendar.YEAR),
                    month = calendarFromDate.value.get(Calendar.MONTH),
                    day = calendarFromDate.value.get(Calendar.DAY_OF_MONTH)
                )
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
                    } else if (iDate < calendarItemList[index].parent.date.toCalendar()) {
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
                    iDate.add(Calendar.DAY_OF_MONTH, 1)
                }
                while (index <= 42) { /* In Calendar Screen, the number of items shown is 42 */
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
                    index += 1
                    iDate.add(Calendar.DAY_OF_MONTH, 1)
                }

                /*
                Used in ItemChartScreen
                 */
                val incomeTotal = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.sumOf { it.amount.toBigDecimal() }?.toString() ?: "0"

                val expenseTotal = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.sumOf { it.amount.toBigDecimal() }?.toString() ?: "0"

                val incomeCategoryList = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.groupingBy { it.categoryCode }
                    ?.reduce { _, acc, ele ->
                        val sum = acc.amount.toBigDecimal() + ele.amount.toBigDecimal()
                        acc.copy(
                            amount = sum.toString()
                        )
                    }
                    ?.values?.toList()
                    ?.sortedByDescending { it.amount.toBigDecimal() } ?: emptyList()

                val expenseCategoryList = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.groupingBy { it.categoryCode }
                    ?.reduce { _, acc, ele ->
                        val sum = acc.amount.toBigDecimal() + ele.amount.toBigDecimal()
                        acc.copy(
                            amount = sum.toString()
                        )
                    }
                    ?.values?.toList()
                    ?.sortedByDescending { it.amount.toBigDecimal() } ?: emptyList()

                val itemMapByCategoryIncome = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_INCOME }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
                    ?.groupBy { it.categoryCode } ?: emptyMap()

                val itemMapByCategoryExpense = result.data
                    ?.filter { it.categoryColor == UtilCategory.CATEGORY_COLOR_EXPENSE }
                    ?.filter {
                        if (searchId.value != 0L) {
                            true
                        } else {
                            it.eventDate.toCalendar()
                                .get(Calendar.MONTH) == cal.value.get(Calendar.MONTH)
                        }
                    }
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
                                UiText.DynamicString(result.message ?: "Unknown error.")
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
                _eventFlow.emit(UiEvent.LoadingCompleted)
            }.launchIn(viewModelScope)
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: UiText) : UiEvent()
        object LoadingCompleted : UiEvent()
    }

}