package com.kakeibo.feature_main.presentation.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.getYMDDateText
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate

abstract class BaseViewModel : ViewModel() {

    private val _localEventDate = mutableStateOf(
        UtilDate.getTodaysLocalDate().getYMDDateText(UtilDate.DATE_FORMAT_DB)
    )
    val localEventDate: State<String> = _localEventDate

    abstract fun onDateChanged()

    fun resetToToday() {
        _localEventDate.value = UtilDate.getTodaysLocalDate().getYMDDateText(UtilDate.DATE_FORMAT_DB)
        onDateChanged()
    }

    fun updateLocalEventDate(localDate: String) {
        _localEventDate.value = localDate
        onDateChanged()
    }

    fun plus(value: Int, dateTimeUnit: DateTimeUnit.DateBased) {
        val localDate = localEventDate.value.toLocalDate().plus(value, dateTimeUnit)

        _localEventDate.value = LocalDate(
            localDate.year, localDate.monthNumber, localDate.dayOfMonth
        ).getYMDDateText(UtilDate.DATE_FORMAT_DB)

        onDateChanged()
    }

}