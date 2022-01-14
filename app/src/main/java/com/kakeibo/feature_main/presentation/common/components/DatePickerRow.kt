package com.kakeibo.feature_main.presentation.common.components

import android.app.DatePickerDialog
import android.content.Context
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.kakeibo.R
import com.kakeibo.util.UtilDate
import com.kakeibo.util.UtilDate.getYDateText
import com.kakeibo.util.UtilDate.getYMDDateText
import com.kakeibo.util.UtilDate.getYMDateTextFromDBFormat
import kotlinx.datetime.*

@Composable
fun DatePickerRow(
    modifier: Modifier = Modifier,
    context: Context,
    type: DateType, // depending on which screen, date text on the button differs
    dateFormatIndex: Int, // date format that's stored in shared pref
//    localDate: LocalDate = UtilDate.getTodaysLocalDate(),
    localDateState: State<LocalDate> = mutableStateOf(UtilDate.getTodaysLocalDate()),
    onTextLayout: (LocalDate) -> Unit
) {
//    val weekNames = stringArrayResource(id = R.array.week_name)
////    val localDateState = remember { mutableStateOf(localDate) }
//
//    Log.d("asdf","localDateState.value="+localDateState.value+" "+localDate+" "+localDate)
//
//    val datePickerDialog = DatePickerDialog(
//        context,
//        { _: DatePicker, y: Int, m: Int, d: Int ->
//            dateText = when (type) {
//                is DateType.YMDW -> {
////                    LocalDate(y, m + 1, d)
//                    LocalDate(y, m + 1, d).toString()
//                }
//                is DateType.YM -> {
////                    LocalDate(y, m + 1, d)
//                    LocalDate(y, m + 1, d).toString()
//                }
//                is DateType.Y -> {
////                    LocalDate(y, m + 1, d)
//                    LocalDate(y, m + 1, d).toString()
//                }
//            }
//        },
//        localDate.year,
//        localDate.monthNumber,
//        localDate.dayOfMonth
//    )
//
//    Row(
//        modifier = modifier,
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.Center
//    ) {
//        IconButton(
//            onClick = {
//                dateText = when (type) {
//                    is DateType.YMDW -> {
////                        mutableStateOf(dateText.value.plus(-1, DateTimeUnit.DAY))
//                        localDate.plus(-1, DateTimeUnit.DAY).toString()
////                        dateText.toLocalDate().plus(-1, DateTimeUnit.DAY).toString()
//                    }
//                    is DateType.YM -> {
////                        mutableStateOf(dateText.value.plus(-1, DateTimeUnit.MONTH))
//                        localDate.plus(-1, DateTimeUnit.MONTH).toString()
////                        dateText.toLocalDate().plus(-1, DateTimeUnit.MONTH).toString()
//                    }
//                    is DateType.Y -> {
////                        mutableStateOf(dateText.value.plus(-1, DateTimeUnit.YEAR))
//                        localDate.plus(-1, DateTimeUnit.YEAR).toString()
////                        dateText.toLocalDate().plus(-1, DateTimeUnit.YEAR).toString()
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
//                        localDate.getYMDDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
////                        dateText.toLocalDate().getYMDDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
//                    }
//                    is DateType.YM -> {
//                        localDate.getYMDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
////                        dateText.toLocalDate().getYMDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
//                    }
//                    is DateType.Y -> {
//                        localDate.getYDateText()
////                        dateText.toLocalDate().getYDateText()
//                    }
//                },
//                onTextLayout = { onTextLayout(localDateState.value) }
//            )
//        }
//        IconButton(
//            onClick = {
//                date = when (type) {
//                    is DateType.YMDW -> { localDate.plus(1, DateTimeUnit.DAY) }
//                    is DateType.YM -> { localDateState.value.plus(1, DateTimeUnit.MONTH) }
//                    is DateType.Y -> { localDateState.value.plus(1, DateTimeUnit.YEAR) }
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





    var dateState by remember { mutableStateOf(localDateState) }
//    val dateText = remember { mutableStateOf(localDate) }
//    Log.d("asdf","dateText="+dateText+" "+localDate+" "+defaultDate)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->
            dateState = when (type) {
                is DateType.YMDW -> { mutableStateOf(LocalDate(y, m + 1, d)) }
                is DateType.YM -> { mutableStateOf(LocalDate(y, m + 1, d)) }
                is DateType.Y -> { mutableStateOf(LocalDate(y, m + 1, d)) }
            }
        },
        localDateState.value.year,
        localDateState.value.monthNumber - 1,
        localDateState.value.dayOfMonth
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = {
                dateState = when (type) {
                    is DateType.YMDW -> {
                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.DAY))
                    }
                    is DateType.YM -> {
                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.MONTH))
                    }
                    is DateType.Y -> {
                        mutableStateOf(dateState.value.plus(-1, DateTimeUnit.YEAR))
                    }
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
                        dateState.value.getYMDDateText(UtilDate.DATE_FORMATS[dateFormatIndex])
                    }
                    is DateType.YM -> {
                        dateState.value.getYMDateTextFromDBFormat(UtilDate.DATE_FORMATS[dateFormatIndex])
                    }
                    is DateType.Y -> {
                        dateState.value.getYDateText()
                    }
                },
                onTextLayout = { onTextLayout(dateState.value) }
            )
        }
        IconButton(
            onClick = {
                dateState = when (type) {
                    is DateType.YMDW -> {
                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.DAY))
                    }
                    is DateType.YM -> {
                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.MONTH))
                    }
                    is DateType.Y -> {
                        mutableStateOf(dateState.value.plus(1, DateTimeUnit.YEAR))
                    }
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

sealed class DateType {
    object YMDW: DateType()
    object YM: DateType()
    object Y: DateType()
}
