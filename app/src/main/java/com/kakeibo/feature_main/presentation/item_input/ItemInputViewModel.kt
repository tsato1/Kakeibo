package com.kakeibo.feature_main.presentation.item_input

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.presentation.TextFieldState
import com.kakeibo.core.util.Resource
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.domain.use_cases.ItemUseCases
import com.kakeibo.feature_main.presentation.common.DateHandleViewModel
import com.kakeibo.util.UtilCurrency
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
class ItemInputViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val displayedCategoryUseCases: DisplayedCategoryUseCases
) : DateHandleViewModel() {

    private val _itemAmount = mutableStateOf(TextFieldState(hint = "Enter amount"))
    val itemAmount: State<TextFieldState> = _itemAmount

    private val _itemMemo = mutableStateOf(TextFieldState(hint = "Enter memo: max 20 characters"))
    val itemMemo: State<TextFieldState> = _itemMemo

    private val _itemCategoryCode = mutableStateOf(0) /* Income by default */
    val itemCategoryCode: State<Int> = _itemCategoryCode

    private val _displayedCategoryState = mutableStateOf(DisplayedCategoryListState())
    val displayedCategoryListState: State<DisplayedCategoryListState> = _displayedCategoryState

    private val _savedItemId = mutableStateOf(-1L)
    val savedItemId: State<Long> = _savedItemId

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var loadDisplayedCategoryListJob: Job? = null

    init {
        loadItems()

//        savedStateHandle.get<Long>("itemId")?.let { itemId ->
//            if (itemId != -1L) { // always 0
//                viewModelScope.launch {
//                    itemUseCases.getItemByIdUseCase(itemId)?.also { item ->
//                        currentItemId = item.id
//
//                        _itemDate.value = itemDate.value
//
//                        _itemAmount.value = itemAmount.value.copy(
//                            text = item.amount.toString(),
//                            isHintVisible = false
//                        )
//
//                        _itemMemo.value = itemMemo.value.copy(
//                            text = item.memo,
//                            isHintVisible = false
//                        )
//
//                        _itemCategoryId.value = itemCategoryId.value
//                    }
//                }
//            }
//        }
    }

    fun onEvent(event: ItemInputEvent) {
        when (event) {
            is ItemInputEvent.EnterAmount -> {
                _itemAmount.value = itemAmount.value.copy(
                    text = event.value
                )
            }
            is ItemInputEvent.ChangeAmountFocus -> {
                _itemAmount.value = itemAmount.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemAmount.value.text.isBlank()
                )
            }
            is ItemInputEvent.EnterMemo -> {
                _itemMemo.value = itemMemo.value.copy(
                    text = event.value
                )
            }
            is ItemInputEvent.ChangeMemoFocus -> {
                _itemMemo.value = itemMemo.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemMemo.value.text.isBlank()
                )
            }
            is ItemInputEvent.SaveItemWithCategory -> {
                viewModelScope.launch {
                    try {
                        itemUseCases.insertItemUseCase(
                            DisplayedItemModel(
                                id = 0, // 0: id is automatically assigned by Room
                                amount = itemAmount.value.text,
                                currencyCode = UtilCurrency.CURRENCY_NONE,
                                categoryCode = event.displayedCategory.code,
                                categoryColor = event.displayedCategory.color,
                                memo = itemMemo.value.text,
                                eventDate = UtilDate.getDBDate(
                                    date.value.split(" ")[0],
                                    appPreferences.getDateFormatIndex()
                                ),
                                updateDate = UtilDate.getTodaysYMD(UtilDate.DATE_FORMAT_DB_KMS),
                            )
                        )
                        _eventFlow.emit(UiEvent.Save)
                    }
                    catch (e: ItemEntity.InvalidItemException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(e.message ?: "Error: Couldn't save an item.")
                        )
                    }
                }
            }
            is ItemInputEvent.DeleteItem -> { }
            else -> { }
        }
    }

    private fun loadItems() {
        loadDisplayedCategoryListJob?.cancel()
        loadDisplayedCategoryListJob = viewModelScope.launch {
            displayedCategoryUseCases.getDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _displayedCategoryState.value = displayedCategoryListState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isLoading = false
                            )
                        }
                        is Resource.Error -> {
                            _displayedCategoryState.value = displayedCategoryListState.value.copy(
                                displayedCategoryList = result.data ?: emptyList(),
                                isLoading = false
                            )
                            _eventFlow.emit(UiEvent.ShowToast(result.message ?: "Unknown Error"))
                        }
                        is Resource.Loading -> {
                            _displayedCategoryState.value = displayedCategoryListState.value.copy(
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
        data class ShowSnackbar(val message: String): UiEvent()
        data class ShowToast(val message: String): UiEvent()
        object Save: UiEvent()
    }
}