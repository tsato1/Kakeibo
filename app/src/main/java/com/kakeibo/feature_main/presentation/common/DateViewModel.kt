package com.kakeibo.feature_main.presentation.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.kakeibo.core.presentation.KkbAppViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate

abstract class DateViewModel : KkbAppViewModel() {

    private val _localEventDate = mutableStateOf(
        UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB)
    )
    val localEventDate: State<String> = _localEventDate

    abstract fun onDateChanged()

    fun resetToToday() {
        _localEventDate.value = UtilDate.getTodaysLocalDate().toYMDString(UtilDate.DATE_FORMAT_DB)
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
        ).toYMDString(UtilDate.DATE_FORMAT_DB)

        onDateChanged()
    }

}