//package com.kakeibo.feature_main.presentation.common
//
//import android.util.Log
//import androidx.compose.runtime.State
//import androidx.compose.runtime.mutableStateOf
//import androidx.lifecycle.ViewModel
//import java.util.*
//
//abstract class DateHandleViewModel : ViewModel() {
//
//    private val _calendarState = mutableStateOf(Calendar.getInstance())
//    val calendarState: State<Calendar> = _calendarState
//
//    fun onDateChanged(calendar: Calendar) {
//        _calendarState.value = calendar
//
//        Log.d("asdf", "calendarstate = " + calendarState.value.time.toString())
//    }
//
//}