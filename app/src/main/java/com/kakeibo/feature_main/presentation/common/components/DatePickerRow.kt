package com.kakeibo.feature_main.presentation.common.components

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kakeibo.R
import com.kakeibo.feature_main.presentation.common.BaseViewModel
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.toYMDString
import com.kakeibo.util.UtilDate.toYMDWString
import com.kakeibo.util.UtilDate.toYMString
import kotlinx.datetime.*

@Composable
fun DatePickerRow(
    modifier: Modifier = Modifier,
    context: Context,
    type: DateType, // depending on which screen, date text on the button differs
    dateFormatIndex: Int, // date format that's stored in shared pref
    viewModel: BaseViewModel
) {
    Log.d("asdf", "dateformatindex = "+dateFormatIndex)
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            viewModel.updateLocalEventDate(
                LocalDate(y, m + 1, d).toYMDString(UtilDate.DATE_FORMAT_DB)
            )
        },
        viewModel.localEventDate.value.toLocalDate().year,
        viewModel.localEventDate.value.toLocalDate().monthNumber - 1,
        viewModel.localEventDate.value.toLocalDate().dayOfMonth
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                when (type) {
                    is DateType.YMDW -> { viewModel.plus(-1, DateTimeUnit.DAY) }
                    is DateType.YM -> { viewModel.plus(-1, DateTimeUnit.MONTH) }
                    is DateType.Y -> { viewModel.plus(-1, DateTimeUnit.YEAR) }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_left_24),
                contentDescription = "Previous"
            )
        }
        TextButton(
            modifier = Modifier.weight(1f),
            onClick = {
                datePickerDialog.show()
            }
        ) {
            Text(
                text =  when (type) {
                    is DateType.YMDW -> {
                        viewModel.localEventDate.value.toLocalDate().toYMDWString(UtilDate.DATE_FORMATS[dateFormatIndex])
                    }
                    is DateType.YM -> {
                        viewModel.localEventDate.value.toLocalDate().toYMString(UtilDate.DATE_FORMATS[dateFormatIndex])
                    }
                    is DateType.Y -> {
                        viewModel.localEventDate.value.toLocalDate().year.toString()
                    }
                }
            )
        }
        IconButton(onClick = { viewModel.resetToToday() }) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
        }
        IconButton(
            onClick = {
                when (type) {
                    is DateType.YMDW -> { viewModel.plus(1, DateTimeUnit.DAY) }
                    is DateType.YM -> { viewModel.plus(1, DateTimeUnit.MONTH) }
                    is DateType.Y -> { viewModel.plus(1, DateTimeUnit.YEAR) }
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_arrow_right_24),
                contentDescription = "Next"
            )
        }
    }
}


//@Composable
//fun DatePickerRow(
//    modifier: Modifier = Modifier,
//    context: Context,
//    type: DateType, // depending on which screen, date text on the button differs
//    dateFormatIndex: Int, // date format that's stored in shared pref
//    localDateState: State<LocalDate> = mutableStateOf(UtilDate.getTodaysLocalDate()),
//    onTextLayout: (LocalDate) -> Unit,
//    onRefresh: () -> Unit
//) {
//    var dateState by remember { mutableStateOf(localDateState) }
//
//    val datePickerDialog = DatePickerDialog(
//        context,
//        { _: DatePicker, y: Int, m: Int, d: Int ->
//            dateState = when (type) {
//                is DateType.YMDW -> { mutableStateOf(LocalDate(y, m + 1, d)) }
//                is DateType.YM -> { mutableStateOf(LocalDate(y, m + 1, d)) }
//                is DateType.Y -> { mutableStateOf(LocalDate(y, m + 1, d)) }
//            }
//        },
//        localDateState.value.year,
//        localDateState.value.monthNumber - 1,
//        localDateState.value.dayOfMonth
//    )
//
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        IconButton(
//            onClick = {
//                dateState = when (type) {
//                    is DateType.YMDW -> {
//                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.DAY))
//                    }
//                    is DateType.YM -> {
//                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.MONTH))
//                    }
//                    is DateType.Y -> {
//                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.YEAR))
//                    }
//                }
//            }
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_baseline_arrow_left_24),
//                contentDescription = "Previous"
//            )
//        }
//        TextButton(
//            modifier = Modifier.weight(1f),
//            onClick = {
//                datePickerDialog.show()
//            }
//        ) {
//            Text(
//                text =  when (type) {
//                    is DateType.YMDW -> {
//                        dateState.value.getYMDDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
//                    }
//                    is DateType.YM -> {
//                        dateState.value.getYMDateTextFromDBFormat(UtilDate.DATE_FORMATS[dateFormatIndex])
//                    }
//                    is DateType.Y -> {
//                        dateState.value.getYDateText()
//                    }
//                },
//                onTextLayout = {
//                    onTextLayout(dateState.value)
//                }
//            )
//        }
//        IconButton(onClick = { onRefresh() }) {
//            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
//        }
//        IconButton(
//            onClick = {
//                dateState = when (type) {
//                    is DateType.YMDW -> {
//                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.DAY))
//                    }
//                    is DateType.YM -> {
//                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.MONTH))
//                    }
//                    is DateType.Y -> {
//                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.YEAR))
//                    }
//                }
//            }
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_baseline_arrow_right_24),
//                contentDescription = "Next"
//            )
//        }
//    }
//}

sealed class DateType {
    object YMDW: DateType()
    object YM: DateType()
    object Y: DateType()
}
