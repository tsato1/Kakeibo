package com.kakeibo.feature_settings.presentation.category_reorder

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.kakeibo.core.data.preferences.AppPreferences
import com.kakeibo.core.util.Resource
import com.kakeibo.feature_settings.domain.models.CategoryModel
import com.kakeibo.feature_settings.domain.use_cases.CategoryRearrangeUseCases
import com.kakeibo.feature_settings.domain.use_cases.KkbAppUseCases
import com.kakeibo.feature_settings.presentation.settings_list.KkbAppState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRearrangeUseCases: CategoryRearrangeUseCases,
    kkbAppUseCases: KkbAppUseCases,
    appPreferences: AppPreferences
) : ViewModel() {

    private val _kkbAppState = mutableStateOf(KkbAppState())
    val kkbAppState: State<KkbAppState> = _kkbAppState

    val numColumns = appPreferences.getNumColumns()

    private val _displayedCategories = MutableLiveData<List<CategoryModel>>()
    val displayedCategories: LiveData<List<CategoryModel>> = _displayedCategories

    var loadDisplayedCategoryListJob: Job? = null

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

        loadDisplayedCategoryListJob?.cancel()
        loadDisplayedCategoryListJob = viewModelScope.launch {
            categoryRearrangeUseCases.getDisplayedCategoriesUseCase()
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            _displayedCategories.value = result.data ?: emptyList()
                        }
                        is Resource.Error -> {
                            _displayedCategories.value = result.data ?: emptyList()
                            // send error
                        }
                        is Resource.Loading -> {
                            _displayedCategories.value = result.data ?: emptyList()
                        }
                    }
                }
                .launchIn(this)
        }
    }

    fun onSave(list: List<CategoryModel>) {
        viewModelScope.launch {
            categoryRearrangeUseCases.updateDisplayedCategoriesUseCase(list)
        }
    }

}