package com.kakeibo.feature_main.presentation.item_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.presentation.TextFieldState
import com.kakeibo.core.util.Resource
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.domain.use_cases.ItemUseCases
import com.kakeibo.feature_main.presentation.item_detail.item_input.DisplayedCategoryListState
import com.kakeibo.util.UtilCurrency
import com.kakeibo.util.UtilDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val displayedCategoryUseCases: DisplayedCategoryUseCases,
    val appPreferences: AppPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    val dateFormatIndex = appPreferences.getDateFormatIndex()

    private val _currentItemId = mutableStateOf(-1L)
    val currentItemId: State<Long> = _currentItemId

    private val _itemDate = mutableStateOf(UtilDate.getTodaysLocalDate())
    val itemDate: State<LocalDate> = _itemDate

    private val _itemAmount = mutableStateOf(TextFieldState(hint = "Enter amount"))
    val itemAmount: State<TextFieldState> = _itemAmount

//    private val _itemCurrencyCode = mutableStateOf(UtilCurrency.CURRENCY_NONE)
//    val itemCurrencyCode: State<String> = _itemCurrencyCode
    private val _itemCategoryCode = mutableStateOf(0) /* Income by default */
    val itemCategoryCode: State<Int> = _itemCategoryCode
    private val _itemCategoryName = mutableStateOf("")
    val itemCategoryName: State<String> = _itemCategoryName
    private val _itemCategoryDrawable = mutableStateOf("ic_category_income")
    val itemCategoryDrawable: State<String> = _itemCategoryDrawable
    private val _itemCategoryImage = mutableStateOf(byteArrayOf())
    val itemCategoryImage: State<ByteArray?> = _itemCategoryImage

    private val _itemMemo = mutableStateOf(TextFieldState(hint = "Enter memo: max 20 characters"))
    val itemMemo: State<TextFieldState> = _itemMemo

    private val _displayedCategoryListState = mutableStateOf(DisplayedCategoryListState())
    val displayedCategoryListState: State<DisplayedCategoryListState> = _displayedCategoryListState

    private val _savedItemId = mutableStateOf(-1L) /* keep the itemId in case undo clicked */
    val savedItemId: State<Long> = _savedItemId

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var loadDisplayedCategoryListJob: Job? = null

    init {
        loadCategories()

        savedStateHandle.get<Long>("itemId")?.let { itemId ->
            if (itemId != -1L) {
                viewModelScope.launch {
                    itemUseCases.getItemByIdUseCase(itemId)?.also { item ->
                        _currentItemId.value = item.id ?: -1L

                        _itemDate.value = item.eventDate.toLocalDate()

                        _itemAmount.value = itemAmount.value.copy(
                            text = item.amount,
                            isHintVisible = false
                        )

//                        _itemCurrencyCode.value = item.currencyCode
                        _itemCategoryCode.value = item.categoryCode
                        _itemCategoryName.value = item.categoryName
                        _itemCategoryDrawable.value = item.categoryDrawable
                        _itemCategoryImage.value = item.categoryImage ?: byteArrayOf()

                        _itemMemo.value = itemMemo.value.copy(
                            text = item.memo,
                            isHintVisible = false
                        )
                    }
                }
            }
        }
    }

    fun onEvent(event: ItemDetailEvent) {
        when (event) {
            is ItemDetailEvent.DateChanged -> {
                _itemDate.value = event.value
            }
            is ItemDetailEvent.AmountEntered -> {
                _itemAmount.value = itemAmount.value.copy(
                    text = event.value
                )
            }
            is ItemDetailEvent.AmountFocusChanged -> {
                _itemAmount.value = itemAmount.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemAmount.value.text.isBlank()
                )
            }
            is ItemDetailEvent.CategorySelected -> {
                _itemCategoryCode.value = event.displayedCategory.code
                _itemCategoryName.value = event.displayedCategory.name
                _itemCategoryDrawable.value = event.displayedCategory.drawable
                _itemCategoryImage.value = event.displayedCategory.image ?: byteArrayOf()
            }
            is ItemDetailEvent.MemoEntered -> {
                _itemMemo.value = itemMemo.value.copy(
                    text = event.value
                )
            }
            is ItemDetailEvent.MemoFocusChanged -> {
                _itemMemo.value = itemMemo.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemMemo.value.text.isBlank()
                )
            }
            is ItemDetailEvent.SaveItemWithCategory -> {
                viewModelScope.launch {
                    try {
                        itemUseCases.insertItemUseCase(
                            DisplayedItemModel(
                                id = 0, // 0: id will be automatically assigned by Room
                                amount = itemAmount.value.text,
                                currencyCode = UtilCurrency.CURRENCY_NONE,
                                categoryCode = event.displayedCategory.code,
                                categoryColor = event.displayedCategory.color,
                                memo = itemMemo.value.text,
                                eventDate = itemDate.value.toString(), // itemDate is in DB format
                                updateDate = UtilDate.getCurrentMoment(UtilDate.DATE_FORMAT_DB_KMS)
                            )
                        )
                        _eventFlow.emit(UiEvent.Save)
                    }
                    catch (e: ItemEntity.InvalidItemException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(e.message ?: "Error: Couldn't save the item.")
                        )
                    }
                }
            }
            is ItemDetailEvent.SaveItem -> {
                viewModelScope.launch {
                    try {
                        itemUseCases.insertItemUseCase(
                            DisplayedItemModel(
                                id = currentItemId.value,
                                amount = itemAmount.value.text,
                                currencyCode = UtilCurrency.CURRENCY_NONE,
                                categoryCode = itemCategoryCode.value,
                                memo = itemMemo.value.text,
                                eventDate = itemDate.value.toString(),
                                updateDate = UtilDate.getCurrentMoment(UtilDate.DATE_FORMAT_DB_KMS)
                            )
                        )
                        _eventFlow.emit(UiEvent.Save)
                    }
                    catch (e: ItemEntity.InvalidItemException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(e.message ?: "Error: Couldn't save the item.")
                        )
                    }
                }
            }
            is ItemDetailEvent.DeleteItem -> {
                // empty
            }
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
                            _eventFlow.emit(UiEvent.ShowToast(result.message ?: "Unknown Error"))
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
        data class ShowSnackbar(val message: String): UiEvent()
        data class ShowToast(val message: String): UiEvent()
        object Save: UiEvent()
    }
}