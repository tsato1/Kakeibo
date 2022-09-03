package com.kakeibo.feature_main.presentation.item_detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.presentation.TextFieldState
import com.kakeibo.core.util.Resource
import com.kakeibo.core.data.local.entities.ItemEntity
import com.kakeibo.core.data.local.entities.SearchEntity
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_main.domain.models.DisplayedItemModel
import com.kakeibo.feature_main.domain.use_cases.DisplayedCategoryUseCases
import com.kakeibo.feature_main.domain.use_cases.DisplayedItemUseCases
import com.kakeibo.feature_main.presentation.common.DateViewModel
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
import javax.inject.Inject

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val displayedItemUseCases: DisplayedItemUseCases,
    private val displayedCategoryUseCases: DisplayedCategoryUseCases,
    val appPreferences: AppPreferences,
    savedStateHandle: SavedStateHandle
) : DateViewModel() {

    val dateFormatIndex = appPreferences.getDateFormatIndex()
    val fractionDigits = appPreferences.getFractionDigits()

    private val _currentItemId = mutableStateOf(-1L)
    val currentItemId: State<Long> = _currentItemId

    private val _itemAmount = mutableStateOf(TextFieldState(hint = "Enter amount"))
    val itemAmountState: State<TextFieldState> = _itemAmount

    private val _itemCategoryCodeState = mutableStateOf(0) /* Income by default */
    val itemCategoryCodeState: State<Int> = _itemCategoryCodeState
    private val _itemCategoryNameState = mutableStateOf("")
    val itemCategoryNameState: State<String> = _itemCategoryNameState
    private val _itemCategoryDrawableState = mutableStateOf("ic_category_income")
    val itemCategoryDrawableState: State<String> = _itemCategoryDrawableState
    private val _itemCategoryImageState = mutableStateOf(byteArrayOf())
    val itemCategoryImageState: State<ByteArray?> = _itemCategoryImageState
    private val _itemMemoState = mutableStateOf(TextFieldState(hint = "Enter memo: max 20 characters"))
    val itemMemoState: State<TextFieldState> = _itemMemoState

    private val _displayedCategoryListState = mutableStateOf(DisplayedCategoryListState())
    val displayedCategoryListState: State<DisplayedCategoryListState> = _displayedCategoryListState

    /* keep the itemId in case undo clicked */
//    private val _savedItemId = mutableStateOf(-1L)
//    val savedItemId: State<Long> = _savedItemId

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    var loadDisplayedCategoryListJob: Job? = null

    init {
        loadCategories()

        savedStateHandle.get<Long>("itemId")?.let { itemId ->
            if (itemId != -1L) {
                viewModelScope.launch {
                    displayedItemUseCases.getItemByIdUseCase(itemId)?.also { item ->
                        _currentItemId.value = item.id ?: -1L

                        updateLocalEventDate(item.eventDate)

                        _itemAmount.value = itemAmountState.value.copy(
                            text = item.amount,
                            isHintVisible = false
                        )

                        _itemCategoryCodeState.value = item.categoryCode
                        _itemCategoryNameState.value = item.categoryName
                        _itemCategoryDrawableState.value = item.categoryDrawable
                        _itemCategoryImageState.value = item.categoryImage ?: byteArrayOf()

                        _itemMemoState.value = itemMemoState.value.copy(
                            text = item.memo,
                            isHintVisible = false
                        )
                    }
                }
            }
        }
    }

    override fun onDateChanged() {
    }

    fun onEvent(event: ItemDetailEvent) {
        when (event) {
            is ItemDetailEvent.AmountEntered -> {
                _itemAmount.value = itemAmountState.value.copy(
                    text = event.value
                )
            }
            is ItemDetailEvent.AmountFocusChanged -> {
                _itemAmount.value = itemAmountState.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemAmountState.value.text.isBlank()
                )
            }
            is ItemDetailEvent.CategorySelected -> {
                _itemCategoryCodeState.value = event.displayedCategory.code
                _itemCategoryNameState.value = event.displayedCategory.name
                _itemCategoryDrawableState.value = event.displayedCategory.drawable
                _itemCategoryImageState.value = event.displayedCategory.image ?: byteArrayOf()
            }
            is ItemDetailEvent.MemoEntered -> {
                _itemMemoState.value = itemMemoState.value.copy(
                    text = event.value
                )
            }
            is ItemDetailEvent.MemoFocusChanged -> {
                _itemMemoState.value = itemMemoState.value.copy(
                    isHintVisible = !event.focusState.isFocused && itemMemoState.value.text.isBlank()
                )
            }
            is ItemDetailEvent.SaveItem -> {
                viewModelScope.launch {
                    try {
                        val focusItemId = displayedItemUseCases.insertItemUseCase(
                            displayedItemModel = DisplayedItemModel(
                                id = if (currentItemId.value == -1L) 0 else currentItemId.value,
                                amount = itemAmountState.value.text,
                                currencyCode = UtilCurrency.CURRENCY_NONE,
                                categoryCode = itemCategoryCodeState.value,
                                memo = itemMemoState.value.text,
                                eventDate = localEventDate.value,
                                updateDate = UtilDate.getCurrentMoment(UtilDate.DATE_FORMAT_DB_KMS)
                            ),
                            syncWithRemote = kkbAppModelState.value.kkbAppModel.intVal3
                        )
                        _eventFlow.emit(UiEvent.Save(localEventDate.value, focusItemId))
                    }
                    catch (e: ItemEntity.InvalidItemException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(e.message ?: "Error: Couldn't save the item.")
                            )
                        )
                    }
                    catch (e: SearchEntity.InvalidSearchException) {
                        _eventFlow.emit(
                            UiEvent.ShowSnackbar(
                                UiText.DynamicString(e.message ?: "Error: Invalid Search.")
                            )
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
            displayedCategoryUseCases.getDisplayedCategoryUseCase()
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
                                UiEvent.ShowToast(
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
        data class ShowToast(val message: UiText): UiEvent()
        data class Save(val focusDate: String, val focusItemId: Long): UiEvent()
    }
}