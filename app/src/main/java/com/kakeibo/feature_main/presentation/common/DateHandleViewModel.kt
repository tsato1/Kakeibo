package com.kakeibo.feature_main.presentation.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kakeibo.core.presentation.BaseViewModel
import com.kakeibo.util.UtilDate

abstract class DateHandleViewModel : BaseViewModel() {

    private val _date = mutableStateOf(UtilDate.getTodaysYMD(UtilDate.DATE_FORMAT_YMD))
    val date: State<String> = _date

    fun onDateChanged(date: String) {
        _date.value = date
    }

}