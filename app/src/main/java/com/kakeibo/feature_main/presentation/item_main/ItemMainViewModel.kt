package com.kakeibo.feature_main.presentation.item_main

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.use_cases.ItemUseCases
import com.kakeibo.feature_main.presentation.common.DateHandleViewModel
import com.kakeibo.feature_main.presentation.item_main.item_list.ExpandableItemListState
import com.kakeibo.feature_main.presentation.item_main.item_list.ItemListEvent
import com.kakeibo.feature_main.presentation.item_main.item_list.components.ExpandableItem
import com.kakeibo.util.UtilDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemMainViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    savedStateHandle: SavedStateHandle
) : DateHandleViewModel() {

    private val _expandableItemListState = mutableStateOf(ExpandableItemListState())
    val expandableItemListState: State<ExpandableItemListState> = _expandableItemListState

    private var recentlyDeletedDisplayedItemModel: DisplayedItemModel? = null

    private var getItemsJob: Job? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        savedStateHandle.get<Long>("itemId")?.let { itemId ->
            getItemsYM(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
        }
    }

    private fun getItemsYM(ym: String) {
        getItemsJob?.cancel()
        getItemsJob = itemUseCases.getItemListByYearMonthUseCase(ym)
            .onEach { result ->
                val expandableItemList = result.data
                    ?.groupBy {
                        it.eventDate
                    }
                    ?.map { entry ->
                        ExpandableItem(
                            ExpandableItem.Parent(
                                entry.key,
                                entry.value
                                    .map {
                                        it.amount.toLong()
                                    }
                                    .filter {
                                        it > 0
                                    }
                                    .sum()
                                    .toString()
                                ,
                                entry.value
                                    .map {
                                        it.amount.toLong()
                                    }
                                    .filter {
                                        it < 0
                                    }
                                    .sum()
                                    .toString()
                            ),
                            entry.value
                        )
                    }
                    ?: emptyList()

                when (result) {
                    is Resource.Success -> {
                        _expandableItemListState.value = expandableItemListState.value.copy(
                            expandableItemList = expandableItemList,
                            isLoading = false
                        )
                    }
                    is Resource.Error -> {
                        _expandableItemListState.value = expandableItemListState.value.copy(
                            expandableItemList = expandableItemList,
                            isLoading = false
                        )
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                result.message ?: "At ItemListViewModel: Unknown error."
                            )
                        )
                    }
                    is Resource.Loading -> {
                        _expandableItemListState.value = expandableItemListState.value.copy(
                            expandableItemList = expandableItemList,
                            isLoading = true
                        )
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: ItemListEvent) {
        when (event) {
            is ItemListEvent.DeleteItem -> {
                viewModelScope.launch {
                    itemUseCases.deleteItemUseCase(event.displayedItemModel)
                    recentlyDeletedDisplayedItemModel = event.displayedItemModel
                }
            }
            is ItemListEvent.RestoreItem -> {
                viewModelScope.launch {
                    itemUseCases.insertItemUseCase( // todo catch exception
                        recentlyDeletedDisplayedItemModel ?: return@launch
                    )
                    recentlyDeletedDisplayedItemModel = null
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String): UiEvent()
    }

//    private val _forceUpdate = MutableLiveData(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
//
//    private val _items = _forceUpdate.switchMap {
//        repository.getItemsByYearMonth(it).asLiveData()
//    }
//    val items: LiveData<List<Item>> = _items
//
//    fun setItemsThisMonth() { // to go back to default report (this month)
//        _forceUpdate.postValue(UtilDate.getTodaysYM(UtilDate.DATE_FORMAT_DB))
//    }
//    fun setItemsCustomYearMonth(y: String, m: String) {
//        _forceUpdate.postValue("$y-$m")
//    }
//
//    /* used in ReportFragment */
//    val expandableItems: LiveData<List<ExpandableItem>> =
//        Transformations.map(items) { items ->
//            items.map {
//                ExpandableItem(
//                    it.id.toString(),
//                    ExpandableItem.Parent(it.eventDate),
//                    listOf(ExpandableItem.Child(it))
//                )
//            }
//        }

//        items.switchMap { items ->
//            items.map {
//                ExpandableItem(
//                    it.id,
//                    ExpandableItem.Parent(it.eventDate),
//                    ExpandableItem.Child(it)
//                )
//            }
//        }

//    private val itemsByCategory: LiveData<Map<Pair<Int, BigDecimal>, List<Item>>> =
//        Transformations.map(items) { items ->
//            items
//                .groupBy { it.categoryCode }
//                .mapKeys { entry -> Pair(entry.key, entry.value.sumOf {it.amount} ) }
//        }
//    /* used in PieGraph in ReportC */
//    val itemsIncome: LiveData<List<Pair<Int, BigDecimal>>> =
//        Transformations.map(itemsByCategory) { map ->
//            map.keys
//                .map { it }
//                .filter { it.second > BigDecimal(0) }
//                .map { Pair(it.first, it.second) }
//                .sortedWith(compareBy({it.second}, {it.first}))
//                .asReversed()
//        }
//    /* used in PieGraph in ReportC */
//    val itemsExpense: LiveData<List<Pair<Int, BigDecimal>>> = Transformations.map(itemsByCategory) { map ->
//        map.keys
//            .map { it }
//            .filter { it.second < BigDecimal(0) }
//            .map { Pair(it.first, it.second.abs()) }
//            .sortedWith(compareBy({it.second}, {it.first}))
//            .asReversed()
//    }
//
//    val income: LiveData<BigDecimal> = Transformations.map(items) { items ->
//        items.filter { it.amount > BigDecimal(0) }.sumOf { it.amount }
//    }
//    val expense: LiveData<BigDecimal> = Transformations.map(items) { items ->
//        items.filter { it.amount < BigDecimal(0) }.sumOf { it.amount }
//    }
//    val balance: LiveData<BigDecimal> = Transformations.map(items) { items ->
//        items.sumOf { it.amount }
//    }

//    suspend fun insert(item: Item) {
//        repository.insertItem(item)
//    }
//
//    suspend fun insertAll(items: List<Item>) {
//        repository.insertItems(items)
//    }
//
//    suspend fun deleteAndInsertAll(items: List<Item>) {
//        repository.deleteAllItems()
//        repository.insertItems(items)
//    }
//
//    suspend fun deleteAll() {
//        repository.deleteAllItems()
//    }
//
//    suspend fun delete(id: Long) {
//        repository.deleteItem(id)
//    }

    /*
    need to change
    do this process in db
     */
//    fun isCategoryAlreadyUsed(code: Int): Boolean {
//        all.value?.let { all ->
//            all.forEach {
//                if (it.categoryCode == code) return true }
//        }
//        return false
//    }

}