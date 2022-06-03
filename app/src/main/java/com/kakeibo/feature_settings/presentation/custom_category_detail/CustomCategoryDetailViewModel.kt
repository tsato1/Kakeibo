package com.kakeibo.feature_settings.presentation.custom_category_detail

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.presentation.TextFieldState
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.use_cases.CustomCategoryUseCases
import com.kakeibo.feature_settings.domain.use_cases.KkbAppUseCases
import com.kakeibo.feature_settings.presentation.settings_list.KkbAppState
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDrawing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCategoryDetailViewModel @Inject constructor(
    private val customCategoryUseCases: CustomCategoryUseCases,
    kkbAppUseCases: KkbAppUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _kkbAppState = mutableStateOf(KkbAppState())
    val kkbAppState: State<KkbAppState> = _kkbAppState

    private val _categoryType = mutableStateOf(UtilCategory.CATEGORY_COLOR_EXPENSE)
    val categoryType: State<Int> = _categoryType

    private val _categoryName = mutableStateOf(TextFieldState(hint = "Max 8 characters"))
    val categoryName: State<TextFieldState> = _categoryName

    private val _categoryImage = mutableStateOf(
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    )
    val categoryImage: State<Bitmap> = _categoryImage

    private var currentCategoryId = mutableStateOf(-1L)
    val categoryId: State<Long> = currentCategoryId

    private var categoryCode: Int = -1

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var getKkbAppEntityJob: Job? = null

    init {
        getKkbAppEntityJob?.cancel()
        getKkbAppEntityJob = kkbAppUseCases.getKkbAppUseCase()
            .onEach { result ->
                _kkbAppState.value = kkbAppState.value.copy(
                    id = result.id,
                    name = result.name,
                    type = result.type,
                    intVal1 = result.valInt1,
                    intVal2 = result.valInt2,
                    intVal3 = result.valInt3,
                    strVal1 = result.valStr1,
                    strVal2 = result.valStr2,
                    strVal3 = result.valStr3
                )
            }
            .launchIn(viewModelScope)

        savedStateHandle.get<Long>("categoryId")?.let { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    customCategoryUseCases.getCustomCategoryByIdUseCase(id).also { categoryModel ->
                        currentCategoryId.value = categoryModel?._id ?: -1L
                        categoryCode = categoryModel?.code ?: 1
                        _categoryType.value = categoryModel?.color ?: 0
                        _categoryName.value = categoryName.value.copy(
                            text = categoryModel?.name ?: "null",
                            isHintVisible = false
                        )
                        _categoryImage.value = UtilDrawing.bytesToBitmap(
                            categoryModel?.image ?: ByteArray(0)
                        )
                    }
                    _eventFlow.emit(UiEvent.Init)
                }
            }
        }
        savedStateHandle.get<Int>("categoryCode")?.let { newCode ->
            categoryCode = newCode
        }
    }

    fun onEvent(event: CustomCategoryDetailEvent) {
        when (event) {
            is CustomCategoryDetailEvent.TypeChanged -> {
                _categoryType.value = event.type
            }
            is CustomCategoryDetailEvent.NameEntered -> {
                _categoryName.value = categoryName.value.copy(text = event.name)
            }
            is CustomCategoryDetailEvent.NameFocusChanged -> {
                _categoryName.value = categoryName.value.copy(
                    isHintVisible = !event.focusState.isFocused && categoryName.value.text.isBlank()
                )
            }
            is CustomCategoryDetailEvent.Save -> {
                viewModelScope.launch {
                    try {
                        customCategoryUseCases.insertCustomCategoryUseCase(
                            CategoryModel(
                                _id = if (currentCategoryId.value == -1L) 0 else currentCategoryId.value,
                                code = categoryCode,
                                name = categoryName.value.text,
                                color = categoryType.value,
                                sign = UtilCategory.CATEGORY_SIGN_CUS,
                                drawable = "",
                                image = UtilDrawing.bitmapToBytes(event.bitmap),
                                parent = UtilCategory.CATEGORY_PARENT_NON,
                                description = "",
                                savedDate = UtilDate.getCurrentMoment(UtilDate.DATE_FORMAT_DB_KMS)
//                                isSynced = false
                            )
                        )
                        _eventFlow.emit(UiEvent.Save)
                    } catch (e: CategoryModel.InvalidCustomCategoryException) {
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                UiText.DynamicString(e.message ?: "Unknown Error")
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        object Init: UiEvent()
        data class ShowSnackbar(val message: UiText): UiEvent()
        data class ShowToast(val message: UiText): UiEvent()
        object Save: UiEvent()
    }

}