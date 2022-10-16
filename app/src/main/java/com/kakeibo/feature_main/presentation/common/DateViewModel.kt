package com.kakeibo.feature_main.presentation.common

import androidx.lifecycle.SavedStateHandle
import com.kakeibo.core.presentation.KkbAppViewModel
import com.kakeibo.util.UtilDate.of
import com.kakeibo.util.UtilDate.toCalendar
import java.util.*

abstract class DateViewModel constructor(
    private val savedStateHandle: SavedStateHandle
) : KkbAppViewModel() {

    val cal = savedStateHandle.getStateFlow("cal", Calendar.getInstance())

    init {
        savedStateHandle["cal"] = Calendar.getInstance()
    }

    abstract fun onDateChanged()

    fun resetToToday() {
        savedStateHandle["cal"] = Calendar.getInstance()
        onDateChanged()
    }

    /*
     expects db format
     */
    fun updateLocalEventDate(stringDate: String) {
        savedStateHandle["cal"] = stringDate.toCalendar()
        onDateChanged()
    }

    fun plus(unit: Int, value: Int) {
        cal.value.add(unit, value)
        val y = cal.value.get(Calendar.YEAR)
        val m = cal.value.get(Calendar.MONTH)
        val d = cal.value.get(Calendar.DAY_OF_MONTH)
        savedStateHandle["cal"] = Calendar.getInstance().of(y, m, d)
        onDateChanged()
    }

}