package com.kakeibo.feature_settings.presentation.settings_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.kakeibo.R
import com.kakeibo.core.data.preferences.AppPreferencesImpl
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_settings.domain.use_cases.ItemUseCases
import com.kakeibo.core.presentation.KkbAppViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    val appPreferences: AppPreferencesImpl
) : KkbAppViewModel() {

    private val _dateFormatIndexState = mutableStateOf(0)
    val dateFormatIndexState: State<Int> = _dateFormatIndexState

    private val _fractionDigitsIndexState = mutableStateOf(0)
    val fractionDigitsIndexState: State<Int> = _fractionDigitsIndexState

    private val _numColumnsIndexState = mutableStateOf(1)
    val numColumnsIndexState: State<Int> = _numColumnsIndexState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun setSharedPreferencesStates() {
        _dateFormatIndexState.value = appPreferences.getDateFormatIndex()
        _fractionDigitsIndexState.value = appPreferences.getFractionDigitsIndex()
        _numColumnsIndexState.value = appPreferences.getNumColumnsIndex()
    }

    fun onEvent(event: SettingsListEvent, index: Int) {
        when (event) {
            is SettingsListEvent.DateFormatChanged -> {
                appPreferences.set(R.string.pref_key_date_format, index)
                _dateFormatIndexState.value = appPreferences.getDateFormatIndex()
            }
            is SettingsListEvent.FractionDigitsChanged -> {
                appPreferences.set(R.string.pref_key_fraction_digits, index)
                _fractionDigitsIndexState.value = appPreferences.getFractionDigitsIndex()
            }
            is SettingsListEvent.NumColumnsChanged -> {
                appPreferences.set(R.string.pref_key_num_columns, index)
                _numColumnsIndexState.value = appPreferences.getNumColumnsIndex()
            }
            is SettingsListEvent.DeleteAllItems -> {
                viewModelScope.launch {
                    val result = itemUseCases.deleteAllItemsUseCase()
                    if (result == 0) {
                        _eventFlow.emit(
                            UiEvent.ShowToast(UiText.StringResource(R.string.msg_nothing_tp_delete))
                        )
                    }
                    else {
                        _eventFlow.emit(
                            UiEvent.ShowToast(UiText.StringResource(R.string.msg_all_delete_success))
                        )
                    }
                }
            }
            is SettingsListEvent.ShowAds -> {
                viewModelScope.launch {
                    val result = super.showAds()

                    if (result == 1L) { /* success (result should be 1L because the first row id is 1) */
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                UiText.StringResource(R.string.msg_access_to_category_management)
                            )
                        )
                    }
                    else { /* error */
                        _eventFlow.emit(
                            UiEvent.ShowToast(UiText.StringResource(R.string.error))
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class ShowSnackBar(val stringId: UiText): UiEvent()
        data class ShowToast(val stringId: UiText): UiEvent()
    }

}