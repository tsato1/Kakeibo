package com.kakeibo.feature_settings.presentation.custom_category_detail.presentation

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.core.presentation.TextFieldState
import com.kakeibo.core.util.UiEvent
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.use_cases.CustomCategoryUseCases
import com.kakeibo.util.UtilCategory
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDrawing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCategoryDetailViewModel @Inject constructor(
    private val customCategoryUseCases: CustomCategoryUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

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

    init {
        savedStateHandle.get<Long>("categoryId")?.let { id ->
            if (id != -1L) {
                viewModelScope.launch {
                    customCategoryUseCases.getCustomCategoryByIdUseCase(id).also { categoryModel ->
                        currentCategoryId.value = categoryModel._id
                        categoryCode = categoryModel.code
                        _categoryType.value = categoryModel.color
                        _categoryName.value = categoryName.value.copy(
                            text = categoryModel.name,
                            isHintVisible = false
                        )
                        _categoryImage.value = UtilDrawing.bytesToBitmap(
                            categoryModel.image ?: ByteArray(0)
                        )
                    }
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
                                savedDate = UtilDate.getTodaysYMD(UtilDate.DATE_FORMAT_DB_KMS)
//                                isSynced = false
                            )
                        )
                        _eventFlow.emit(UiEvent.Save)
                    } catch (e: CategoryModel.InvalidCustomCategoryException) {
                        _eventFlow.emit(UiEvent.ShowToast(e.message ?: "Unknown Error"))
                    }
                }
            }
        }
    }

}