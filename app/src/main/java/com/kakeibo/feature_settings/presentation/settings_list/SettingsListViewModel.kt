package com.kakeibo.feature_settings.presentation.settings_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakeibo.R
import com.kakeibo.core.data.local.entities.KkbAppEntity
import com.kakeibo.core.data.preferences.AppPreferencesImpl
import com.kakeibo.core.util.UiText
import com.kakeibo.feature_settings.domain.use_cases.ItemUseCases
import com.kakeibo.feature_settings.domain.use_cases.KkbAppUseCases
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    private val itemUseCases: ItemUseCases,
    private val kkbAppUseCases: KkbAppUseCases,
    val appPreferences: AppPreferencesImpl
) : ViewModel() {

    private val _kkbAppState = mutableStateOf(KkbAppState())
    val kkbAppState: State<KkbAppState> = _kkbAppState

    private val _keyDateFormatState = mutableStateOf(0)
    val keyDateFormatIndexState: State<Int> = _keyDateFormatState

    private val _keyFractionDigitsState = mutableStateOf(0)
    val keyFractionDigitsIndexState: State<Int> = _keyFractionDigitsState

    private val _keyNumColumnsState = mutableStateOf(1)
    val keyNumColumnsIndexState: State<Int> = _keyNumColumnsState

    private var getKkbAppEntityJob: Job? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

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
    }

    fun setSharedPreferencesStates() {
        _keyDateFormatState.value = appPreferences.getDateFormatIndex()
        _keyFractionDigitsState.value = appPreferences.getFractionDigitsIndex()
        _keyNumColumnsState.value = appPreferences.getNumColumnsIndex()
    }

    fun onEvent(event: SettingsListEvent, index: Int) {
        when (event) {
            is SettingsListEvent.DateFormatChanged -> {
                appPreferences.set(R.string.pref_key_date_format, index)
                _keyDateFormatState.value = appPreferences.getDateFormatIndex()
            }
            is SettingsListEvent.FractionDigitsChanged -> {
                appPreferences.set(R.string.pref_key_fraction_digits, index)
                _keyFractionDigitsState.value = appPreferences.getFractionDigitsIndex()
            }
            is SettingsListEvent.NumColumnsChanged -> {
                appPreferences.set(R.string.pref_key_num_columns, index)
                _keyNumColumnsState.value = appPreferences.getNumColumnsIndex()
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
                    val result = kkbAppUseCases.insertKkbAppUseCase(
                        KkbAppEntity(
                            kkbAppState.value.id,
                            kkbAppState.value.name,
                            kkbAppState.value.type,
                            UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB_KMS),
                            kkbAppState.value.intVal1,
                            0,
                            kkbAppState.value.intVal3,
                            kkbAppState.value.strVal1,
                            kkbAppState.value.strVal2,
                            kkbAppState.value.strVal3
                        )
                    )

                    if (result == -1L) { /* error */
                        _eventFlow.emit(
                            UiEvent.ShowToast(UiText.StringResource(R.string.error))
                        )
                    }
                    else { /* success (result should be 1L because the first row id is 1) */
                        _eventFlow.emit(
                            UiEvent.ShowToast(
                                UiText.StringResource(R.string.msg_access_to_category_management)
                            )
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