package com.kakeibo.feature_settings.presentation.settings_list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kakeibo.R
import com.kakeibo.core.AppPreferencesImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsListViewModel @Inject constructor(
    val appPreferences: AppPreferencesImpl
) : ViewModel() {

    private val _keyDateFormatState = mutableStateOf(0)
    val keyDateFormatIndexState: State<Int> = _keyDateFormatState

    private val _keyFractionDigitsState = mutableStateOf(0)
    val keyFractionDigitsIndexState: State<Int> = _keyFractionDigitsState

    private val _keyNumColumnsState = mutableStateOf(1)
    val keyNumColumnsIndexState: State<Int> = _keyNumColumnsState

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
                _keyFractionDigitsState.value = appPreferences.getDateFormatIndex()
            }
            is SettingsListEvent.NumColumnsChanged -> {
                appPreferences.set(R.string.pref_key_num_columns, index)
                _keyNumColumnsState.value = appPreferences.getNumColumnsIndex()
            }
        }
    }

}